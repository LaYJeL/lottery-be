package com.game.lottery.controller;

import com.game.lottery.dto.*;
import com.game.lottery.security.CurrentUser;
import com.game.lottery.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<WalletDto> getWallet() {
        UUID userId = CurrentUser.get();
        log.debug("User {} requested wallet summary", userId);
        return ResponseEntity.ok(walletService.getWalletSummary(userId));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TransactionDto>> getTransactions(Pageable pageable) {
        UUID userId = CurrentUser.get();
        log.debug("User {} requested transactions page", userId);
        return ResponseEntity.ok(walletService.getTransactions(userId, pageable));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDto> deposit(@Valid @RequestBody DepositRequest request) {
        UUID userId = CurrentUser.get();
        log.info("User {} initiating deposit of {}", userId, request.getAmount());
        return ResponseEntity.ok(walletService.deposit(userId, request));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDto> withdraw(@Valid @RequestBody WithdrawRequest request) {
        UUID userId = CurrentUser.get();
        log.info("User {} initiating withdrawal of {}", userId, request.getAmount());
        return ResponseEntity.ok(walletService.withdraw(userId, request));
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<PaymentMethodDto>> getPaymentMethods() {
        UUID userId = CurrentUser.get();
        log.debug("User {} requested payment methods", userId);
        return ResponseEntity.ok(walletService.getPaymentMethods(userId));
    }

    @PostMapping("/payment-methods")
    public ResponseEntity<PaymentMethodDto> addPaymentMethod(@Valid @RequestBody AddPaymentMethodRequest request) {
        UUID userId = CurrentUser.get();
        log.info("User {} adding new payment method: {}", userId, request.getType());
        return ResponseEntity.ok(walletService.addPaymentMethod(userId, request));
    }
}
