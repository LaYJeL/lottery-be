package com.game.lottery.dto;

import com.game.lottery.enums.TaskActionType;
import com.game.lottery.enums.TaskCategory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TaskDto {

    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Category is required")
    private TaskCategory category;

    @NotNull(message = "Action type is required")
    private TaskActionType actionType;

    private String conditionValue;

    @Min(value = 1, message = "Target count must be at least 1")
    @Builder.Default
    private Integer targetCount = 1;

    @Min(value = 0, message = "Reward points cannot be negative")
    @Builder.Default
    private Integer rewardPoints = 0;

    private BigDecimal rewardCurrency;

    private String requiredLevel;

    private String icon;

    @Builder.Default
    private boolean isActive = true;
}
