package com.game.lottery.service;

import com.game.lottery.config.GameBalanceConfig;
import com.game.lottery.enums.VerificationType;
import com.game.lottery.model.VerificationCode;
import com.game.lottery.repository.VerificationCodeRepository;
import com.game.lottery.service.notification.SmsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final SmsNotificationService smsNotificationService;
    private final GameBalanceConfig gameBalanceConfig;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void generateAndSendPhoneVerificationCode(UUID userId, String phoneNumber) {
        String code = String.format("%06d", random.nextInt(1000000));

        VerificationCode verificationCode = VerificationCode.builder()
                .userId(userId)
                .code(code)
                .type(VerificationType.PHONE_VERIFY)
                .expiresAt(LocalDateTime.now().plusMinutes(gameBalanceConfig.getVerificationCodeExpiryMinutes()))
                .build();

        verificationCodeRepository.save(verificationCode);

        String message = "Your verification code is: " + code;
        smsNotificationService.sendSms(phoneNumber, message);

        log.info("Generated phone verification code for user {}", userId);
    }

    @Transactional
    public boolean verifyPhoneCode(UUID userId, String code) {
        VerificationCode verificationCode = verificationCodeRepository
                .findFirstByUserIdAndTypeOrderByCreatedAtDesc(userId, VerificationType.PHONE_VERIFY)
                .orElseThrow(() -> new IllegalArgumentException("No verification code found"));

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("User {} attempted to verify with expired code", userId);
            throw new IllegalArgumentException("Verification code expired");
        }

        if (verificationCode.getAttempts() >= gameBalanceConfig.getMaxVerificationAttempts()) {
            log.warn("User {} exceeded verification attempts", userId);
            throw new IllegalArgumentException("Too many failed attempts");
        }

        if (!verificationCode.getCode().equals(code)) {
            verificationCode.setAttempts(verificationCode.getAttempts() + 1);
            verificationCodeRepository.save(verificationCode);
            log.warn("User {} provided invalid verification code. Attempt {}/3", userId,
                    verificationCode.getAttempts());
            return false;
        }

        // Code matched
        // Ideally we should mark it as used or delete it to prevent replay,
        // but for now checking expiry and relying on latest is verified is okay-ish.
        // Better: Delete it or mark used.
        verificationCodeRepository.delete(verificationCode);

        log.info("User {} successfully verified phone code", userId);
        return true;
    }
}
