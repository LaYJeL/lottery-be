package com.game.lottery.repository;

import com.game.lottery.enums.VerificationType;
import com.game.lottery.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {
    Optional<VerificationCode> findByUserIdAndType(UUID userId, VerificationType type);
    Optional<VerificationCode> findFirstByUserIdAndTypeOrderByCreatedAtDesc(UUID userId, VerificationType type);

    void deleteByUserIdAndType(UUID userId, VerificationType type);
}
