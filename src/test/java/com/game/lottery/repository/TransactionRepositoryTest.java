package com.game.lottery.repository;

import com.game.lottery.TestcontainersConfiguration;
import com.game.lottery.enums.AuthenticationProvider;
import com.game.lottery.enums.TransactionStatus;
import com.game.lottery.enums.TransactionType;
import com.game.lottery.model.Transaction;
import com.game.lottery.model.User;
import com.game.lottery.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ TestcontainersConfiguration.class, com.game.lottery.security.JpaAuditingConfig.class })
@ActiveProfiles("test")
public class TransactionRepositoryTest {

        @Autowired
        private TestEntityManager entityManager;

        @Autowired
        private TransactionRepository transactionRepository;

        @Test
        void sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter_shouldReturnCorrectSum() {
                // Arrange
                User user = new User();
                user.setKeycloakSub("test-sub-" + UUID.randomUUID());
                user.setAuthProvider(AuthenticationProvider.LOCAL);
                entityManager.persist(user);

                Wallet wallet = user.getWallet();

                createTransaction(wallet, TransactionType.DEPOSIT, TransactionStatus.COMPLETED,
                                new BigDecimal("100.00"),
                                Instant.now());
                createTransaction(wallet, TransactionType.DEPOSIT, TransactionStatus.COMPLETED,
                                new BigDecimal("200.00"),
                                Instant.now());
                createTransaction(wallet, TransactionType.DEPOSIT, TransactionStatus.PENDING, new BigDecimal("300.00"),
                                Instant.now()); // Should be ignored
                createTransaction(wallet, TransactionType.WITHDRAWAL, TransactionStatus.COMPLETED,
                                new BigDecimal("50.00"),
                                Instant.now()); // Different type, should be ignored
                createTransaction(wallet, TransactionType.DEPOSIT, TransactionStatus.COMPLETED,
                                new BigDecimal("400.00"),
                                Instant.now().minus(10, ChronoUnit.DAYS)); // Old, but if we filter by yesterday it
                                                                           // should be ignored

                // Act
                BigDecimal sum = transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                                wallet.getId(),
                                TransactionType.DEPOSIT,
                                TransactionStatus.COMPLETED,
                                Instant.now().minus(1, ChronoUnit.DAYS));

                // Assert
                assertThat(sum).isEqualByComparingTo(new BigDecimal("300.00")); // 100 + 200
        }

        @Test
        void sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter_shouldReturnZero_whenNoMatches() {
                // Arrange
                Wallet wallet = Wallet.builder().build();
                entityManager.persist(wallet);

                // Act
                BigDecimal sum = transactionRepository.sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
                                wallet.getId(),
                                TransactionType.DEPOSIT,
                                TransactionStatus.COMPLETED,
                                Instant.now());

                // Assert
                assertThat(sum).isEqualByComparingTo(BigDecimal.ZERO);
        }

        private void createTransaction(Wallet wallet, TransactionType type, TransactionStatus status, BigDecimal amount,
                        Instant createdAt) {
                Transaction transaction = Transaction.builder()
                                .wallet(wallet)
                                .type(type)
                                .status(status)
                                .amount(amount)
                                .createdAt(createdAt)
                                .build();

                entityManager.persist(transaction);

                entityManager.persist(transaction);
                entityManager.flush();

                if (!createdAt.equals(transaction.getCreatedAt())) {
                        entityManager.getEntityManager()
                                        .createNativeQuery(
                                                        "UPDATE lottery.transactions SET created_at = ? WHERE id = ?")
                                        .setParameter(1, createdAt)
                                        .setParameter(2, transaction.getId())
                                        .executeUpdate();
                        entityManager.refresh(transaction);
                }
        }
}
