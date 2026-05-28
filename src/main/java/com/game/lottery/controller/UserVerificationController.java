package com.game.lottery.controller;

import com.game.lottery.dto.VerificationConfirmRequest;
import com.game.lottery.security.CurrentUser;
import com.game.lottery.service.UserVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/me/verification")
@RequiredArgsConstructor
public class UserVerificationController {

    private final UserVerificationService verificationService;

    @PostMapping("/phone/send")
    public ResponseEntity<Void> sendPhoneVerification() {
        UUID userId = CurrentUser.get();
        verificationService.initiatePhoneVerification(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/phone/confirm")
    public ResponseEntity<Void> confirmPhoneVerification(@RequestBody VerificationConfirmRequest request) {
        UUID userId = CurrentUser.get();
        verificationService.confirmPhoneVerification(userId, request.getCode());
        return ResponseEntity.ok().build();
    }
}
