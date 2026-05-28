package com.game.lottery.service;

import com.game.lottery.dto.UserTaskDto;
import com.game.lottery.enums.TaskActionType;
import com.game.lottery.enums.TaskCategory;
import com.game.lottery.enums.TaskStatus;
import com.game.lottery.model.Task;
import com.game.lottery.model.User;
import com.game.lottery.model.UserProfile;
import com.game.lottery.model.UserTaskProgress;
import com.game.lottery.repository.TaskRepository;
import com.game.lottery.repository.UserRepository;
import com.game.lottery.repository.UserTaskProgressRepository;
import com.game.lottery.service.gamification.ConditionEvaluator;
import com.game.lottery.service.gamification.LevelProgressCalculator;
import com.game.lottery.service.gamification.TaskCycleManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GamificationServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private UserTaskProgressRepository progressRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LevelProgressCalculator levelProgressCalculator;
    @Mock
    private ConditionEvaluator conditionEvaluator;
    @Mock
    private TaskCycleManager taskCycleManager;

    @InjectMocks
    private GamificationService gamificationService;

    private User user;
    private UserProfile profile;
    private Task task;

    @BeforeEach
    void setUp() {
        UUID userId = UUID.randomUUID();
        user = new User();
        user.setId(userId);

        profile = new UserProfile();
        profile.setReputation(0);
        profile.setTasksCompleted(0);
        profile.setBalance(BigDecimal.ZERO);
        profile.setTotalWinnings(BigDecimal.ZERO);
        user.setProfile(profile);

        task = Task.builder()
                .id(UUID.randomUUID())
                .title("Test Task")
                .actionType(TaskActionType.LOGIN)
                .category(TaskCategory.DAILY)
                .targetCount(1)
                .rewardPoints(10)
                .rewardCurrency(BigDecimal.ZERO)
                .isActive(true)
                .build();
    }

    @Test
    void processAction_shouldIncrementProgressAndCompleteTask() {
        // Arrange
        when(taskRepository.findByActionTypeAndIsActiveTrue(TaskActionType.LOGIN))
                .thenReturn(List.of(task));
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(progressRepository.findByUserIdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.empty()); // No existing progress
        when(conditionEvaluator.evaluate(any(Task.class), any(User.class), any(Instant.class), any(ZoneId.class)))
                .thenReturn(true);

        // Act
        gamificationService.processAction(user.getId(), TaskActionType.LOGIN, 1);

        // Assert
        verify(progressRepository).save(any(UserTaskProgress.class));
    }

    @Test
    void processAction_shouldNotResetCycle_WhenSameDay() {
        // Arrange
        when(taskRepository.findByActionTypeAndIsActiveTrue(TaskActionType.LOGIN))
                .thenReturn(List.of(task));

        UserTaskProgress existingProgress = UserTaskProgress.builder()
                .user(user)
                .task(task)
                .currentCount(0)
                .status(TaskStatus.IN_PROGRESS)
                .cycleStartAt(Instant.now()) // Just started
                .build();

        when(userRepository.getReferenceById(user.getId())).thenReturn(user);
        when(progressRepository.findByUserIdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.of(existingProgress));
        when(conditionEvaluator.evaluate(any(Task.class), any(User.class), any(Instant.class), any(ZoneId.class)))
                .thenReturn(true);

        // Act
        gamificationService.processAction(user.getId(), TaskActionType.LOGIN, 1);

        // Assert
        assertEquals(1, existingProgress.getCurrentCount());
        assertEquals(TaskStatus.COMPLETED, existingProgress.getStatus());
        verify(progressRepository).save(existingProgress);
    }

    @Test
    void claimReward_shouldAwardPointsAndMarkClaimed() {
        // Arrange
        UserTaskProgress progress = UserTaskProgress.builder()
                .user(user)
                .task(task)
                .currentCount(1)
                .status(TaskStatus.COMPLETED)
                .build();

        when(progressRepository.findByUserIdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.of(progress));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        // Act
        gamificationService.claimReward(user.getId(), task.getId());

        // Assert
        assertEquals(TaskStatus.CLAIMED, progress.getStatus());
        assertEquals(10, profile.getReputation());
        assertEquals(1, profile.getTasksCompleted());
        verify(userRepository).save(user);
        verify(progressRepository).save(progress);
    }

    @Test
    void claimReward_shouldFail_IfTaskNotCompleted() {
        // Arrange
        UserTaskProgress progress = UserTaskProgress.builder()
                .user(user)
                .task(task)
                .currentCount(0)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(progressRepository.findByUserIdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.of(progress));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gamificationService.claimReward(user.getId(), task.getId()));
    }

    @Test
    void claimReward_shouldFail_IfAlreadyClaimed() {
        // Arrange
        UserTaskProgress progress = UserTaskProgress.builder()
                .user(user)
                .task(task)
                .currentCount(1)
                .status(TaskStatus.CLAIMED)
                .build();

        when(progressRepository.findByUserIdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.of(progress));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gamificationService.claimReward(user.getId(), task.getId()));
    }

    @Test
    void getTasksForUser_shouldCalculatePercentage() {
        // Arrange
        task.setTargetCount(10);
        when(taskRepository.findByIsActiveTrue()).thenReturn(List.of(task));

        UserTaskProgress progress = UserTaskProgress.builder()
                .user(user)
                .task(task)
                .currentCount(5)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        when(progressRepository.findByUserIdAndTaskId(user.getId(), task.getId()))
                .thenReturn(Optional.of(progress));

        // Act
        List<UserTaskDto> result = gamificationService.getTasksForUser(user.getId());

        // Assert
        assertEquals(1, result.size());
        assertEquals(50, result.get(0).getProgressPercentage());
        assertEquals(TaskStatus.IN_PROGRESS, result.get(0).getStatus());
    }
}
