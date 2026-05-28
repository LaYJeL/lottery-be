package com.game.lottery.service.gamification;

import com.game.lottery.model.Task;
import com.game.lottery.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.ZoneId;

@Slf4j
@Component
public class ConditionEvaluator {

    public boolean evaluate(Task task, User user, Instant now, ZoneId zone) {
        String condition = task.getConditionValue();
        if (condition == null || condition.trim().isEmpty()) {
            return true;
        }

        if (condition.startsWith("TIME:")) {
            return evaluateTimeCondition(condition, now, zone);
        } else if (condition.startsWith("ROLE:")) {
            return evaluateRoleCondition(condition, user);
        } else if (condition.equals("BIRTHDAY")) {
            return evaluateBirthdayCondition(user, now, zone);
        }

        return false; // Fail for unknown conditions to be safe
    }

    private boolean evaluateTimeCondition(String condition, Instant now, ZoneId zone) {
        // Format: TIME:HH:mm-HH:mm (e.g., TIME:02:00-05:00)
        try {
            String range = condition.substring(5);
            String[] parts = range.split("-");
            if (parts.length == 2) {
                LocalTime start = LocalTime.parse(parts[0]);
                LocalTime end = LocalTime.parse(parts[1]);
                LocalTime current = now.atZone(zone).toLocalTime();

                if (start.isBefore(end)) {
                    return !current.isBefore(start) && !current.isAfter(end);
                } else {
                    // Ranges crossing midnight (e.g. 23:00-01:00)
                    return !current.isBefore(start) || !current.isAfter(end);
                }
            }
        } catch (Exception e) {
            log.warn("Malformed TIME condition: {}", condition, e);
        }
        return false;
    }

    private boolean evaluateRoleCondition(String condition, User user) {
        // Format: ROLE:BETA
        // User entity in this project uses Keycloak for roles
        // Not implemented - returning false to prevent accidental completion
        log.debug("ROLE condition not implemented: {}", condition);
        return false;
    }

    private boolean evaluateBirthdayCondition(User user, Instant now, ZoneId zone) {
        if (user.getProfile() == null || user.getProfile().getBirthDate() == null) {
            return false;
        }
        MonthDay birthDay = MonthDay.from(user.getProfile().getBirthDate());
        MonthDay today = MonthDay.from(now.atZone(zone));
        return birthDay.equals(today);
    }
}
