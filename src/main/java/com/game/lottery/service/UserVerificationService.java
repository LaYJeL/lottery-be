package com.game.lottery.service;

import com.game.lottery.enums.VerificationType;
import com.game.lottery.model.User;
import com.game.lottery.model.VerificationCode;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.security.SecureRandom;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserVerificationService {

    private final VerificationCodeRepository verificationCodeRepository;
    private final UserRepository userRepository;
    private final SmsService smsService;
    private final UserProvisioningService userProvisioningService;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public void initiatePhoneVerification(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        String phone = user.getProfile().getPhone();
        if (phone == null || phone.isBlank()) {
            throw new IllegalArgumentException("User does not have a phone number set");
        }

        verificationCodeRepository.deleteByUserIdAndType(userId, VerificationType.PHONE_VERIFY);

        String code = String.format("%06d", random.nextInt(999999));

        VerificationCode verificationCode = VerificationCode.builder()
                .userId(userId)
                .type(VerificationType.PHONE_VERIFY)
                .code(code)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .createdAt(LocalDateTime.now())
                .attempts(0)
                .build();

        verificationCodeRepository.save(verificationCode);

        smsService.sendSms(phone, "Your verification code: " + code);
        log.info("Phone verification initiated for user {}", userId);
    }

    @Transactional
    public void confirmPhoneVerification(UUID userId, String code) {
        VerificationCode record = verificationCodeRepository
                .findFirstByUserIdAndTypeOrderByCreatedAtDesc(userId, VerificationType.PHONE_VERIFY)
                .orElseThrow(() -> new IllegalArgumentException("No pending verification found"));

        if (record.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Verification code expired");
        }

        if (record.getAttempts() >= 3) {
            throw new IllegalArgumentException("Too many invalid attempts. Request a new code.");
        }

        if (!record.getCode().equals(code)) {
            record.setAttempts(record.getAttempts() + 1);
            verificationCodeRepository.save(record);
            throw new IllegalArgumentException("Invalid code");
        }

        userProvisioningService.markPhoneVerified(userId);
        verificationCodeRepository.delete(record);
        log.info("Phone verification successful for user {}", userId);
    }
}
