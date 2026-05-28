package com.game.lottery.event;

import com.game.lottery.service.GamificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class GamificationEventListener {

    private final GamificationService gamificationService;

    @Async
    @EventListener
    public void handleTaskAction(TaskActionEvent event) {
        log.debug("Received gamification event for user {}: {} x{}",
                event.getUserId(), event.getActionType(), event.getQuantity());

        try {
            gamificationService.processAction(event.getUserId(), event.getActionType(), event.getQuantity());
        } catch (Exception e) {
            log.error("Error processing gamification event", e);
        }
    }
}
