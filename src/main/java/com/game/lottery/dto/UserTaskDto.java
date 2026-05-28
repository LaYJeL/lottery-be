package com.game.lottery.dto;

import com.game.lottery.enums.TaskActionType;
import com.game.lottery.enums.TaskCategory;
import com.game.lottery.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskDto {

    private UUID taskId;
    private String title;
    private String description;
    private TaskCategory category;
    private TaskActionType actionType;
    private Integer targetCount;
    private Integer rewardPoints;
    private BigDecimal rewardCurrency;

    // User Progress fields
    private Integer currentCount;
    private TaskStatus status;
    private Integer progressPercentage;
    private String icon;
}
