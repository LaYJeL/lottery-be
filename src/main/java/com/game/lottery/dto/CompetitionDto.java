package com.game.lottery.dto;

import com.game.lottery.enums.CompetitionStatus;
import com.game.lottery.enums.CompetitionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompetitionDto {
    private UUID id;
    private String title;
    private CompetitionType type;
    private String description;
    private String prize;
    private BigDecimal entryFee;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private CompetitionStatus status;
    private String imageUrl;
    private Integer participantsCount;
    private boolean requiresApproval;
    private boolean isEntered;
}
