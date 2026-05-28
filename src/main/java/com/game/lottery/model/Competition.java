package com.game.lottery.model;

import com.game.lottery.enums.CompetitionStatus;
import com.game.lottery.enums.CompetitionType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "competitions", schema = "lottery")
public class Competition extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CompetitionType type;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String prize;

    @Column(name = "entry_fee", nullable = false)
    private BigDecimal entryFee;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private CompetitionStatus status;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "requires_approval", nullable = false)
    @Builder.Default
    private boolean requiresApproval = false;

    @Column(name = "participants_count")
    @Builder.Default
    private Integer participantsCount = 0;
}
