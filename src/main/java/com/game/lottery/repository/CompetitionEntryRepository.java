package com.game.lottery.repository;

import com.game.lottery.model.CompetitionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetitionEntryRepository extends JpaRepository<CompetitionEntry, UUID> {
    List<CompetitionEntry> findByUser_UserId(UUID userId);

    List<CompetitionEntry> findByCompetition_Id(UUID competitionId);

    Optional<CompetitionEntry> findByCompetition_IdAndUser_UserId(UUID competitionId, UUID userId);

    @Query("SELECT e.competition.id FROM CompetitionEntry e WHERE e.user.userId = :userId AND e.competition.id IN :competitionIds")
    List<UUID> findEnteredCompetitionIds(@Param("userId") UUID userId,
            @Param("competitionIds") List<UUID> competitionIds);
}
