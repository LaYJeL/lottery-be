package com.game.lottery.model;

import com.game.lottery.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_task_progress", schema = "lottery", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "task_id" }) })
public class UserTaskProgress extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "current_count", nullable = false)
    @Builder.Default
    private Integer currentCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    @Builder.Default
    private TaskStatus status = TaskStatus.IN_PROGRESS;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    // To handle resets (e.g., this week's start)
    @Column(name = "cycle_start_at")
    private Instant cycleStartAt;
}
