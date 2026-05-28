package com.game.lottery.model;

import com.game.lottery.enums.TaskActionType;
import com.game.lottery.enums.TaskCategory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "gamification_tasks", schema = "lottery")
public class Task extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", length = 512)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 32)
    private TaskCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false, length = 32)
    private TaskActionType actionType;

    // Optional parameter (e.g., "min_price=10.00")
    @Column(name = "condition_value")
    private String conditionValue;

    @Column(name = "target_count", nullable = false)
    @Builder.Default
    private Integer targetCount = 1;

    @Column(name = "reward_points", nullable = false)
    @Builder.Default
    private Integer rewardPoints = 0;

    @Column(name = "reward_currency")
    private BigDecimal rewardCurrency;

    @Column(name = "required_level")
    private String requiredLevel;

    @Column(name = "icon")
    private String icon;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private boolean isActive = true;
}
