package com.game.lottery.service;

import com.game.lottery.dto.UserTaskDto;
import com.game.lottery.enums.TaskActionType;
import com.game.lottery.enums.TaskStatus;
import com.game.lottery.exception.TaskNotClaimableException;
import com.game.lottery.exception.TaskProgressNotFoundException;
import com.game.lottery.exception.UserNotFoundException;
import com.game.lottery.model.Task;
import com.game.lottery.model.User;
import com.game.lottery.model.UserTaskProgress;
import com.game.lottery.repository.TaskRepository;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.repository.UserTaskProgressRepository;
import com.game.lottery.service.gamification.ConditionEvaluator;
import com.game.lottery.service.gamification.LevelProgressCalculator;
import com.game.lottery.service.gamification.TaskCycleManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GamificationService {

    private final TaskRepository taskRepository;
    private final UserTaskProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final LevelProgressCalculator levelProgressCalculator;
    private final ConditionEvaluator conditionEvaluator;
    private final TaskCycleManager taskCycleManager;

    @Transactional(readOnly = true)
    public List<UserTaskDto> getTasksForUser(UUID userId) {
        // 1. Fetch available tasks
        List<Task> activeTasks = taskRepository.findByIsActiveTrue();

        // 2. Fetch user progress for these tasks
        // (Optimized: fetching all progress for user instead of N+1)
        // Note: For now we can fetch individually or add a method to repo to find by
        // userId
        // Assuming we rely on optional findByUserIdAndTaskId for simplicity or we can
        // add findByUserId
        // Let's iterate for now, or better, fetch all progress for user if we had that
        // method.
        // But our repo has findByUserIdAndTaskId. Only good for specific check.
        // Let's just fetch tasks and map.

        return activeTasks.stream()
                .map(task -> mapToUserTaskDto(task, userId))
                .collect(Collectors.toList());
    }

    @Transactional
    public void claimReward(UUID userId, UUID taskId) {
        UserTaskProgress progress = progressRepository.findByUserIdAndTaskId(userId, taskId)
                .orElseThrow(() -> new TaskProgressNotFoundException("Task progress not found for task: " + taskId));

        if (progress.getStatus() != TaskStatus.COMPLETED) {
            throw new TaskNotClaimableException("Task not completed or already claimed");
        }

        // 1. Mark as Claimed
        progress.setStatus(TaskStatus.CLAIMED);
        progress.setLastUpdatedAt(Instant.now());
        progressRepository.save(progress);

        // 2. Fetch User Profile
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        com.game.lottery.model.UserProfile profile = user.getProfile();

        // 3. Award Points (Reputation)
        int rewardPoints = progress.getTask().getRewardPoints();
        if (rewardPoints > 0) {
            profile.setReputation(profile.getReputation() + rewardPoints);
            profile.setTasksCompleted(profile.getTasksCompleted() + 1);

            // 4. Update Level Progress
            levelProgressCalculator.updateLevelProgress(profile);
        }

        // 5. Award Currency (if any)
        java.math.BigDecimal rewardCurrency = progress.getTask().getRewardCurrency();
        if (rewardCurrency != null && rewardCurrency.compareTo(java.math.BigDecimal.ZERO) > 0) {
            // For now, direct balance update. Ideally should use WalletService for
            // transaction history.
            profile.setBalance(profile.getBalance().add(rewardCurrency));
            profile.setTotalWinnings(profile.getTotalWinnings().add(rewardCurrency));
        }

        userRepository.save(user);
    }

    @Transactional
    public void processAction(UUID userId, TaskActionType actionType, int quantity) {
        List<Task> tasks = taskRepository.findByActionTypeAndIsActiveTrue(actionType);

        if (tasks.isEmpty()) {
            return;
        }

        User user = userRepository.getReferenceById(userId);
        ZoneId zone = ZoneId.systemDefault();
        Instant now = Instant.now();

        for (Task task : tasks) {

            UserTaskProgress progress = progressRepository.findByUserIdAndTaskId(userId, task.getId())
                    .orElseGet(() -> UserTaskProgress.builder()
                            .user(user)
                            .task(task)
                            .currentCount(0)
                            .status(TaskStatus.IN_PROGRESS)
                            .cycleStartAt(now) // Initial cycle start
                            .build());

            // 1. Check Cycle Reset
            taskCycleManager.handleCycleReset(progress, task, now, zone);

            if (progress.getStatus() != TaskStatus.IN_PROGRESS) {
                continue;
            }

            // 2. Check Conditions (Time, Role, etc.)
            if (!conditionEvaluator.evaluate(task, user, now, zone)) {
                continue;
            }

            // 2. Rate Limit: LOGIN actions count max once per day
            if (task.getActionType() == TaskActionType.LOGIN) {
                LocalDate lastUpdateDate = progress.getLastUpdatedAt() != null
                        ? progress.getLastUpdatedAt().atZone(zone).toLocalDate()
                        : null;
                LocalDate today = now.atZone(zone).toLocalDate();
                if (lastUpdateDate != null && lastUpdateDate.equals(today)) {
                    continue;
                }
            }

            progress.setCurrentCount(progress.getCurrentCount() + quantity);
            progress.setLastUpdatedAt(now);

            if (progress.getCurrentCount() >= task.getTargetCount()) {
                progress.setStatus(TaskStatus.COMPLETED);
            }

            progressRepository.save(progress);
        }
    }

    private UserTaskDto mapToUserTaskDto(Task task, UUID userId) {
        UserTaskProgress progress = progressRepository.findByUserIdAndTaskId(userId, task.getId())
                .orElse(null);

        int currentCount = progress != null ? progress.getCurrentCount() : 0;
        TaskStatus status = progress != null ? progress.getStatus() : TaskStatus.IN_PROGRESS;

        // Calculate percentage
        int percentage = 0;
        if (task.getTargetCount() > 0) {
            percentage = (int) Math.min(100, ((double) currentCount / task.getTargetCount()) * 100);
        }

        // Override status if completed but not marked in DB yet (for safety)
        if (status == TaskStatus.IN_PROGRESS && currentCount >= task.getTargetCount()) {
            percentage = 100;
            // logic usually handles status transition, so we trust status mostly
        }

        return UserTaskDto.builder()
                .taskId(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .category(task.getCategory())
                .actionType(task.getActionType())
                .targetCount(task.getTargetCount())
                .rewardPoints(task.getRewardPoints())
                .rewardCurrency(task.getRewardCurrency())
                .currentCount(currentCount)
                .status(status)
                .progressPercentage(percentage)
                .icon(task.getIcon())
                .build();
    }
}
