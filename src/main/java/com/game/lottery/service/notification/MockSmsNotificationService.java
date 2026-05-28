package com.game.lottery.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockSmsNotificationService implements SmsNotificationService {

    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("============== MOCK SMS ==============");
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("======================================");
    }
}
