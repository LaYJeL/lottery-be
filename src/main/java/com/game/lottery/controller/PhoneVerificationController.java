package com.game.lottery.controller;

import com.game.lottery.dto.PhoneVerificationDto;
import com.game.lottery.model.User;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.security.CurrentUser;
import com.game.lottery.service.UserProvisioningService;
import com.game.lottery.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/users/me/phone")
@RequiredArgsConstructor
public class PhoneVerificationController {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;
    private final UserProvisioningService userProvisioningService;

    @PostMapping("/otp")
    public ResponseEntity<Void> requestOtp() {
        UUID currentUserId = CurrentUser.get();
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (user.getProfile().getPhone() == null || user.getProfile().getPhone().isBlank()) {
            throw new IllegalArgumentException("User does not have a phone number set. Please update profile first.");
        }

        if (user.getChecklist().isPhoneVerified()) {
            // Optional: Return bad request or just ok
            // For now, allow re-verification if they want, or maybe block.
            // Let's block implies it's already done.
            // But maybe they changed phone number?
            // If they changed phone number, isPhoneVerified should have been reset to
            // false.
            // (We need to ensure that in updateProfile! I'll check that next)
        }

        log.info("User {} requested OTP", currentUserId);
        verificationCodeService.generateAndSendPhoneVerificationCode(currentUserId, user.getProfile().getPhone());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<Void> verifyOtp(@RequestBody PhoneVerificationDto request) {
        UUID currentUserId = CurrentUser.get();

        log.info("User {} verifying OTP", currentUserId);
        boolean verified = verificationCodeService.verifyPhoneCode(currentUserId, request.getCode());

        if (verified) {
            userProvisioningService.markPhoneVerified(currentUserId);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
