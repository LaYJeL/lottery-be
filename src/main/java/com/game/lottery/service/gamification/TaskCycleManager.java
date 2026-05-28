package com.game.lottery.service.gamification;

import com.game.lottery.enums.TaskCategory;
import com.game.lottery.enums.TaskStatus;
import com.game.lottery.model.Task;
import com.game.lottery.model.UserTaskProgress;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.IsoFields;

@Component
public class TaskCycleManager {

    public void handleCycleReset(UserTaskProgress progress, Task task, Instant now, ZoneId zone) {
        if (progress.getCycleStartAt() == null) {
            progress.setCycleStartAt(now);
            return;
        }

        boolean shouldReset = shouldResetCycle(progress, task, now, zone);

        if (shouldReset) {
            progress.setCurrentCount(0);
            progress.setStatus(TaskStatus.IN_PROGRESS);
            progress.setCycleStartAt(now);
        }
    }

    private boolean shouldResetCycle(UserTaskProgress progress, Task task, Instant now, ZoneId zone) {
        TaskCategory category = task.getCategory();

        if (category == TaskCategory.DAILY) {
            return shouldResetDaily(progress, now, zone);
        } else if (category == TaskCategory.WEEKLY) {
            return shouldResetWeekly(progress, now, zone);
        }

        // ONE_TIME and other categories don't reset
        return false;
    }

    private boolean shouldResetDaily(UserTaskProgress progress, Instant now, ZoneId zone) {
        LocalDate cycleDate = progress.getCycleStartAt().atZone(zone).toLocalDate();
        LocalDate today = now.atZone(zone).toLocalDate();
        return today.isAfter(cycleDate);
    }

    private boolean shouldResetWeekly(UserTaskProgress progress, Instant now, ZoneId zone) {
        long daysBetween = ChronoUnit.DAYS.between(
                progress.getCycleStartAt().atZone(zone).toLocalDate(),
                now.atZone(zone).toLocalDate()
        );

        if (daysBetween >= 7) {
            int cycleWeek = progress.getCycleStartAt().atZone(zone).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            int currentWeek = now.atZone(zone).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
            return currentWeek != cycleWeek;
        }

        return false;
    }
}
