package com.game.lottery.repository;

import com.game.lottery.model.UserTaskProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserTaskProgressRepository extends JpaRepository<UserTaskProgress, UUID> {
    Optional<UserTaskProgress> findByUserIdAndTaskId(UUID userId, UUID taskId);
}
