package com.game.lottery.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MockSmsService implements SmsService {
    @Override
    public void sendSms(String phoneNumber, String message) {
        log.info("============== MOCK SMS SERVICE ==============");
        log.info("To: {}", phoneNumber);
        log.info("Message: {}", message);
        log.info("==============================================");
    }
}
