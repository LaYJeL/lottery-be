package com.game.lottery.service.notification;

public interface SmsNotificationService {
    void sendSms(String phoneNumber, String message);
}
