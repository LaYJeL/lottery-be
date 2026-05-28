package com.game.lottery.repository;

import com.game.lottery.enums.CompetitionStatus;
import com.game.lottery.model.Competition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CompetitionRepository extends JpaRepository<Competition, UUID> {
    Page<Competition> findByStatus(CompetitionStatus status, Pageable pageable);
}
