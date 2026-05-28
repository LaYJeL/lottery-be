package com.game.lottery.service;

import com.game.lottery.enums.TransactionStatus;
import com.game.lottery.enums.TransactionType;
import com.game.lottery.model.Transaction;
import com.game.lottery.model.Wallet;
import com.game.lottery.repository.TransactionRepository;
import com.game.lottery.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankSimulationService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final org.springframework.context.ApplicationEventPublisher eventPublisher;

    @Scheduled(fixedRate = 30000) // 30 seconds
    @Transactional
    public void processPendingTransactions() {
        List<Transaction> pendingTransactions = transactionRepository.findByStatus(TransactionStatus.PROCESSING);

        if (!pendingTransactions.isEmpty()) {
            log.info("Bank Simulation: Processing {} pending transactions...", pendingTransactions.size());

            for (Transaction tx : pendingTransactions) {
                simulateBankProcessing(tx);
            }
        }
    }

    private void simulateBankProcessing(Transaction tx) {
        // Here we simulate that the bank approved the transaction
        tx.setStatus(TransactionStatus.COMPLETED);

        if (tx.getType() == TransactionType.DEPOSIT) {
            // For deposits, we only add funds AFTER bank confirmation
            Wallet wallet = tx.getWallet();
            wallet.setBalance(wallet.getBalance().add(tx.getAmount()));
            walletRepository.save(wallet);
            log.info("Bank Simulation: Deposit completed. Added {} {} to wallet ID {}",
                    tx.getAmount(), wallet.getCurrency(), wallet.getId());

            // Publish Gamification Event
            eventPublisher.publishEvent(new com.game.lottery.event.TaskActionEvent(
                    this,
                    wallet.getUser().getId(),
                    com.game.lottery.enums.TaskActionType.DEPOSIT,
                    1));
        } else if (tx.getType() == TransactionType.WITHDRAWAL) {
            // For withdrawals, funds were already deducted/reserved. We just confirm
            // completion.
            log.info("Bank Simulation: Withdrawal completed for wallet ID {}", tx.getWallet().getId());
        }

        transactionRepository.save(tx);
    }
}
