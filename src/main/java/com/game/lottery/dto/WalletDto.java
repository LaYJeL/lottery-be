package com.game.lottery.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class WalletDto {
    private UUID id;
    private BigDecimal balance;
    private String currency;
    private WalletMonthlyStatsDto monthlyStats;
}
