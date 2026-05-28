package com.game.lottery.service;

import com.game.lottery.dto.AddPaymentMethodRequest;
import com.game.lottery.dto.DepositRequest;
import com.game.lottery.dto.PaymentMethodDto;
import com.game.lottery.dto.TransactionDto;
import com.game.lottery.dto.WalletDto;
import com.game.lottery.dto.WalletMonthlyStatsDto;
import com.game.lottery.dto.WithdrawRequest;
import com.game.lottery.enums.PaymentMethodType;
import com.game.lottery.enums.TransactionStatus;
import com.game.lottery.enums.TransactionType;
import com.game.lottery.exception.UserNotFoundException;
import com.game.lottery.exception.WalletNotFoundException;
import com.game.lottery.mapper.PaymentMethodMapper;
import com.game.lottery.mapper.TransactionMapper;
import com.game.lottery.model.PaymentMethod;
import com.game.lottery.model.Transaction;
import com.game.lottery.model.User;
import com.game.lottery.model.Wallet;
import com.game.lottery.repository.PaymentMethodRepository;
import com.game.lottery.repository.TransactionRepository;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletService {

        private final WalletRepository walletRepository;
        private final TransactionRepository transactionRepository;
        private final PaymentMethodRepository paymentMethodRepository;
        private final UserRepository userRepository;
        private final org.springframework.context.ApplicationEventPublisher eventPublisher;
        private final TransactionMapper transactionMapper;
        private final PaymentMethodMapper paymentMethodMapper;

        public WalletDto getWalletSummary(UUID userId) {
                Wallet wallet = getOrCreateWallet(userId);

                Instant startOfMonth = YearMonth.now().atDay(1).atStartOfDay(ZoneId.systemDefault())
                                .toInstant();

                BigDecimal totalDeposited = transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                                wallet.getId(), TransactionType.DEPOSIT, TransactionStatus.COMPLETED, startOfMonth);

                BigDecimal totalWithdrawn = transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                                wallet.getId(), TransactionType.WITHDRAWAL, TransactionStatus.COMPLETED, startOfMonth);

                BigDecimal totalSpentTickets = transactionRepository
                                .sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                                                wallet.getId(), TransactionType.TICKET_PURCHASE,
                                                TransactionStatus.COMPLETED, startOfMonth);

                BigDecimal totalSpentCompetition = transactionRepository
                                .sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                                                wallet.getId(), TransactionType.COMPETITION_ENTRY,
                                                TransactionStatus.COMPLETED, startOfMonth);

                BigDecimal totalSpent = totalSpentTickets.add(totalSpentCompetition).abs();

                WalletMonthlyStatsDto stats = WalletMonthlyStatsDto.builder()
                                .totalDeposited(totalDeposited)
                                .totalWithdrawn(totalWithdrawn.abs()) // Returns negative for withdrawals usually, so
                                                                      // absolute value for
                                                                      // display
                                .totalSpent(totalSpent)
                                .build();

                return WalletDto.builder()
                                .id(wallet.getId())
                                .balance(wallet.getBalance())
                                .currency(wallet.getCurrency())
                                .monthlyStats(stats)
                                .build();
        }

        private Wallet getOrCreateWallet(UUID userId) {
                return walletRepository.findByUserId(userId)
                                .orElseGet(() -> {
                                        try {
                                                User user = userRepository.findById(userId)
                                                                .orElseThrow(() -> new UserNotFoundException(
                                                                                "User not found with id: " + userId));
                                                Wallet wallet = Wallet.builder().user(user).build();
                                                return walletRepository.save(wallet);
                                        } catch (org.springframework.dao.DataIntegrityViolationException e) {
                                                // Handle race condition: another thread created the wallet
                                                return walletRepository.findByUserId(userId)
                                                                .orElseThrow(() -> new WalletNotFoundException(
                                                                                "Wallet creation failed for user: " + userId,
                                                                                e));
                                        }
                                });
        }

        @Transactional
        public TransactionDto deposit(UUID userId, DepositRequest request) {
                Wallet wallet = getOrCreateWallet(userId);
                PaymentMethod paymentMethod = null;
                if (request.getPaymentMethodId() != null) {
                        paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId()).orElse(null);
                }

                BigDecimal amount = request.getAmount();

                Transaction transaction = Transaction.builder()
                                .wallet(wallet)
                                .type(TransactionType.DEPOSIT)
                                .amount(amount)
                                .status(TransactionStatus.PROCESSING)
                                .description("Deposit funds")
                                .paymentMethod(paymentMethod)
                                .build();

                transaction = transactionRepository.save(transaction);
                return transactionMapper.toDto(transaction);
        }

        @Transactional
        public TransactionDto withdraw(UUID userId, WithdrawRequest request) {
                Wallet wallet = getOrCreateWallet(userId);
                PaymentMethod paymentMethod = null;
                if (request.getPaymentMethodId() != null) {
                        paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId()).orElse(null);
                }

                BigDecimal amount = request.getAmount();

                if (wallet.getBalance().compareTo(amount) < 0) {
                        throw new com.game.lottery.exception.InsufficientFundsException("Insufficient funds");
                }

                wallet.setBalance(wallet.getBalance().subtract(amount));
                walletRepository.save(wallet);

                Transaction transaction = Transaction.builder()
                                .wallet(wallet)
                                .type(TransactionType.WITHDRAWAL)
                                .amount(amount.negate())
                                .status(TransactionStatus.PROCESSING)
                                .description("Withdrawal request")
                                .paymentMethod(paymentMethod)
                                .build();

                transaction = transactionRepository.save(transaction);
                return transactionMapper.toDto(transaction);
        }

        public Page<TransactionDto> getTransactions(UUID userId, Pageable pageable) {
                Wallet wallet = getOrCreateWallet(userId);
                return transactionRepository.findByWalletId(wallet.getId(), pageable)
                                .map(transactionMapper::toDto);
        }

        public List<PaymentMethodDto> getPaymentMethods(UUID userId) {
                return paymentMethodRepository.findByUserId(userId).stream()
                                .map(paymentMethodMapper::toDto)
                                .collect(Collectors.toList());
        }

        @Transactional
        public PaymentMethodDto addPaymentMethod(UUID userId, AddPaymentMethodRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

                PaymentMethod paymentMethod = PaymentMethod.builder()
                                .user(user)
                                .type(PaymentMethodType.valueOf(request.getType()))
                                .identifier(request.getIdentifier())
                                .label(request.getLabel())
                                .isPrimary(paymentMethodRepository.findByUserId(userId).isEmpty()) // First one is
                                                                                                   // primary
                                .build();

                paymentMethod = paymentMethodRepository.save(paymentMethod);

                user.getChecklist().updatePaymentStatus(true);
                userRepository.save(user);

                return paymentMethodMapper.toDto(paymentMethod);
        }

        @Transactional
        public void spendForCompetition(UUID userId, BigDecimal amount, String description) {
                // Use pessimistic lock to prevent race conditions on balance updates
                Wallet wallet = walletRepository.findByUserIdWithLock(userId)
                                .orElseGet(() -> getOrCreateWallet(userId));

                if (wallet.getBalance().compareTo(amount) < 0) {
                        throw new com.game.lottery.exception.InsufficientFundsException("Insufficient funds");
                }

                wallet.setBalance(wallet.getBalance().subtract(amount));
                walletRepository.save(wallet);

                Transaction transaction = Transaction.builder()
                                .wallet(wallet)
                                .type(TransactionType.COMPETITION_ENTRY)
                                .amount(amount.negate())
                                .status(TransactionStatus.COMPLETED)
                                .description(description)
                                .build();

                transactionRepository.save(transaction);

                // Publish Gamification Event
                eventPublisher.publishEvent(new com.game.lottery.event.TaskActionEvent(
                                this,
                                userId,
                                com.game.lottery.enums.TaskActionType.ENTER_COMPETITION,
                                1));
        }
}
