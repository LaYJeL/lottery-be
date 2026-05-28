package com.game.lottery.dto;

import com.game.lottery.enums.CompetitionType;
import com.game.lottery.enums.EntryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CompetitionEntryDto {
    private UUID id;
    private UUID competitionId;
    private String competitionTitle;
    private CompetitionType competitionType;
    private String content;
    private EntryStatus status;
    private LocalDateTime submittedAt;
    private Integer votes;
}
