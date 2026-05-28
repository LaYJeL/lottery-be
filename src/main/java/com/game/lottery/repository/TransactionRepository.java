package com.game.lottery.repository;

import com.game.lottery.enums.TransactionStatus;
import com.game.lottery.enums.TransactionType;
import com.game.lottery.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findByWalletId(UUID walletId, Pageable pageable);

    Page<Transaction> findByWalletIdAndType(UUID walletId, TransactionType type, Pageable pageable);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.wallet.id = :walletId AND t.type = :type AND t.status = :status AND t.createdAt >= :fromDate")
    BigDecimal sumAmountByWalletIdAndTypeAndStatusAndCreatedAtAfter(
            @Param("walletId") UUID walletId,
            @Param("type") TransactionType type,
            @Param("status") TransactionStatus status,
            @Param("fromDate") Instant fromDate);

    List<Transaction> findByStatus(TransactionStatus status);
}
