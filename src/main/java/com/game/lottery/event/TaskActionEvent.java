package com.game.lottery.event;

import com.game.lottery.enums.TaskActionType;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class TaskActionEvent extends ApplicationEvent {

    private final UUID userId;
    private final TaskActionType actionType;
    private final int quantity;

    public TaskActionEvent(Object source, UUID userId, TaskActionType actionType, int quantity) {
        super(source);
        this.userId = userId;
        this.actionType = actionType;
        this.quantity = quantity;
    }
}
