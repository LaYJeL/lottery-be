package com.game.lottery.repository;

import com.game.lottery.enums.TaskCategory;
import com.game.lottery.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByIsActiveTrue();

    List<Task> findByCategoryAndIsActiveTrue(TaskCategory category);

    List<Task> findByActionTypeAndIsActiveTrue(com.game.lottery.enums.TaskActionType actionType);
}
