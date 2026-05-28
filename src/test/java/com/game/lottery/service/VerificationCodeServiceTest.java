package com.game.lottery.service;

import com.game.lottery.config.GameBalanceConfig;
import com.game.lottery.enums.VerificationType;
import com.game.lottery.model.VerificationCode;
import com.game.lottery.repository.VerificationCodeRepository;
import com.game.lottery.service.notification.SmsNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationCodeServiceTest {

    @Mock
    private VerificationCodeRepository verificationCodeRepository;

    @Mock
    private SmsNotificationService smsNotificationService;

    @Mock
    private GameBalanceConfig gameBalanceConfig;

    @InjectMocks
    private VerificationCodeService verificationCodeService;

    private final UUID userId = UUID.randomUUID();
    private final String phoneNumber = "+1234567890";

    @BeforeEach
    void setUp() {
        lenient().when(gameBalanceConfig.getVerificationCodeExpiryMinutes()).thenReturn(5);
        lenient().when(gameBalanceConfig.getMaxVerificationAttempts()).thenReturn(3);
    }

    @Test
    void generateAndSendPhoneVerificationCode_shouldGenerateAndSaveCode() {
        verificationCodeService.generateAndSendPhoneVerificationCode(userId, phoneNumber);

        ArgumentCaptor<VerificationCode> codeCaptor = ArgumentCaptor.forClass(VerificationCode.class);
        verify(verificationCodeRepository).save(codeCaptor.capture());
        verify(smsNotificationService).sendSms(eq(phoneNumber), anyString());

        VerificationCode savedCode = codeCaptor.getValue();
        assertEquals(userId, savedCode.getUserId());
        assertEquals(VerificationType.PHONE_VERIFY, savedCode.getType());
        assertNotNull(savedCode.getCode());
        assertEquals(6, savedCode.getCode().length());
        assertTrue(savedCode.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    void verifyPhoneCode_shouldReturnTrue_whenCodeValid() {
        String code = "123456";
        VerificationCode verificationCode = VerificationCode.builder()
                .userId(userId)
                .code(code)
                .type(VerificationType.PHONE_VERIFY)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();

        when(verificationCodeRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(userId,
                VerificationType.PHONE_VERIFY))
                .thenReturn(Optional.of(verificationCode));

        boolean result = verificationCodeService.verifyPhoneCode(userId, code);

        assertTrue(result);
        verify(verificationCodeRepository).delete(verificationCode);
    }

    @Test
    void verifyPhoneCode_shouldThrow_whenExpired() {
        VerificationCode expiredCode = VerificationCode.builder()
                .userId(userId)
                .code("123456")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        when(verificationCodeRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(userId,
                VerificationType.PHONE_VERIFY))
                .thenReturn(Optional.of(expiredCode));

        assertThrows(IllegalArgumentException.class, () -> verificationCodeService.verifyPhoneCode(userId, "123456"));
    }

    @Test
    void verifyPhoneCode_shouldIncrementAttempts_whenInvalid() {
        VerificationCode verificationCode = VerificationCode.builder()
                .userId(userId)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .attempts(0)
                .build();

        when(verificationCodeRepository.findFirstByUserIdAndTypeOrderByCreatedAtDesc(userId,
                VerificationType.PHONE_VERIFY))
                .thenReturn(Optional.of(verificationCode));

        boolean result = verificationCodeService.verifyPhoneCode(userId, "WRONG");

        assertFalse(result);
        verify(verificationCodeRepository).save(verificationCode);
        assertEquals(1, verificationCode.getAttempts());
    }
}
