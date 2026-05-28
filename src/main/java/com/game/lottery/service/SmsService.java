package com.game.lottery.service;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
}
