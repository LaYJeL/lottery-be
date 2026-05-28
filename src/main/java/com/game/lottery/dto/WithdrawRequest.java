package com.game.lottery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class WithdrawRequest {
    @jakarta.validation.constraints.NotNull(message = "Amount is required")
    @jakarta.validation.constraints.DecimalMin(value = "1.00", message = "Minimum withdrawal is 1.00")
    private BigDecimal amount;

    private UUID paymentMethodId;
}
