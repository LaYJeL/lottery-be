package com.game.lottery.service;

import com.game.lottery.dto.AddPaymentMethodRequest;
import com.game.lottery.dto.DepositRequest;
import com.game.lottery.dto.PaymentMethodDto;
import com.game.lottery.dto.TransactionDto;
import com.game.lottery.dto.WalletDto;
import com.game.lottery.dto.WithdrawRequest;
import com.game.lottery.enums.TransactionStatus;
import com.game.lottery.enums.TransactionType;
import com.game.lottery.mapper.PaymentMethodMapper;
import com.game.lottery.mapper.TransactionMapper;
import com.game.lottery.model.PaymentMethod;
import com.game.lottery.model.Transaction;
import com.game.lottery.model.User;
import com.game.lottery.model.UserOnboarding;
import com.game.lottery.model.Wallet;
import com.game.lottery.repository.PaymentMethodRepository;
import com.game.lottery.repository.TransactionRepository;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.repository.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private PaymentMethodRepository paymentMethodRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @Mock
    private TransactionMapper transactionMapper;
    @Mock
    private PaymentMethodMapper paymentMethodMapper;

    @InjectMocks
    private WalletService walletService;

    @Test
    void getWalletSummary_shouldCalculateStatsCorrectly() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(UUID.randomUUID()).balance(new BigDecimal("100.00")).build();

        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        // Mock repository sums
        when(transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                eq(wallet.getId()), eq(TransactionType.DEPOSIT), eq(TransactionStatus.COMPLETED), any(Instant.class)))
                .thenReturn(new BigDecimal("500.00"));

        when(transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                eq(wallet.getId()), eq(TransactionType.WITHDRAWAL), eq(TransactionStatus.COMPLETED),
                any(Instant.class)))
                .thenReturn(new BigDecimal("-200.00"));

        when(transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                eq(wallet.getId()), eq(TransactionType.TICKET_PURCHASE), eq(TransactionStatus.COMPLETED),
                any(Instant.class)))
                .thenReturn(new BigDecimal("-50.00"));

        when(transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                eq(wallet.getId()), eq(TransactionType.COMPETITION_ENTRY), eq(TransactionStatus.COMPLETED),
                any(Instant.class)))
                .thenReturn(new BigDecimal("-20.00"));

        // Act
        WalletDto result = walletService.getWalletSummary(userId);

        // Assert
        assertEquals(new BigDecimal("100.00"), result.getBalance());
        assertEquals(new BigDecimal("500.00"), result.getMonthlyStats().getTotalDeposited());
        assertEquals(new BigDecimal("200.00"), result.getMonthlyStats().getTotalWithdrawn()); // Should be abs
        assertEquals(new BigDecimal("70.00"), result.getMonthlyStats().getTotalSpent()); // 50 + 20
    }

    @Test
    void deposit_shouldCreateTransaction() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(UUID.randomUUID()).build();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        DepositRequest request = new DepositRequest();
        request.setAmount(new BigDecimal("100.00"));

        TransactionDto expectedDto = TransactionDto.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.PROCESSING)
                .amount(new BigDecimal("100.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            t.setCreatedAt(Instant.now());
            return t;
        });
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(expectedDto);

        // Act
        TransactionDto result = walletService.deposit(userId, request);

        // Assert
        assertNotNull(result.getId());
        assertEquals(TransactionType.DEPOSIT, result.getType());
        assertEquals(TransactionStatus.PROCESSING, result.getStatus());
        assertEquals(new BigDecimal("100.00"), result.getAmount());

        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void withdraw_shouldDeductBalanceAndCreateTransaction() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(UUID.randomUUID()).balance(new BigDecimal("200.00")).build();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(new BigDecimal("100.00"));

        TransactionDto expectedDto = TransactionDto.builder()
                .id(UUID.randomUUID())
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.PROCESSING)
                .amount(new BigDecimal("-100.00"))
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction t = invocation.getArgument(0);
            t.setId(UUID.randomUUID());
            t.setCreatedAt(Instant.now());
            return t;
        });
        when(transactionMapper.toDto(any(Transaction.class))).thenReturn(expectedDto);

        // Act
        TransactionDto result = walletService.withdraw(userId, request);

        // Assert
        assertEquals(new BigDecimal("100.00"), wallet.getBalance()); // 200 - 100
        verify(walletRepository).save(wallet);

        assertEquals(TransactionType.WITHDRAWAL, result.getType());
        assertEquals(new BigDecimal("-100.00"), result.getAmount());
    }

    @Test
    void withdraw_shouldThrowException_whenInsufficientFunds() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Wallet wallet = Wallet.builder().id(UUID.randomUUID()).balance(new BigDecimal("50.00")).build();
        when(walletRepository.findByUserId(userId)).thenReturn(Optional.of(wallet));

        WithdrawRequest request = new WithdrawRequest();
        request.setAmount(new BigDecimal("100.00"));

        // Act & Assert
        assertThrows(com.game.lottery.exception.InsufficientFundsException.class,
                () -> walletService.withdraw(userId, request));
    }

    @Test
    void addPaymentMethod_shouldUpdateUserChecklist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        UserOnboarding checklist = UserOnboarding.empty(user);
        user.setChecklist(checklist);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenAnswer(i -> {
            PaymentMethod pm = i.getArgument(0);
            pm.setId(UUID.randomUUID());
            return pm;
        });

        AddPaymentMethodRequest request = new AddPaymentMethodRequest();
        request.setType("CREDIT_CARD");
        request.setIdentifier("1234");
        request.setLabel("My Card");

        PaymentMethodDto expectedDto = PaymentMethodDto.builder()
                .id(UUID.randomUUID())
                .label("My Card")
                .identifier("1234")
                .build();
        when(paymentMethodMapper.toDto(any(PaymentMethod.class))).thenReturn(expectedDto);

        // Act
        walletService.addPaymentMethod(userId, request);

        // Assert
        assertTrue(user.getChecklist().isPaymentMethodPresent(), "Payment method present flag should be true");
    }
}
