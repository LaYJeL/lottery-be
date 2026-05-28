package com.game.lottery.dto;

import com.game.lottery.enums.PaymentMethodType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PaymentMethodDto {
    private UUID id;
    private PaymentMethodType type;
    private String label;
    private String identifier; // mask or email
    private boolean isPrimary;
}
