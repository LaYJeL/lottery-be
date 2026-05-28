package com.game.lottery.controller;

import com.game.lottery.dto.UserTaskDto;
import com.game.lottery.security.CurrentUser;
import com.game.lottery.service.GamificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class UserTaskController {

    private final GamificationService gamificationService;

    @GetMapping
    public ResponseEntity<List<UserTaskDto>> getMyTasks() {
        UUID currentUserId = CurrentUser.get();
        List<UserTaskDto> tasks = gamificationService.getTasksForUser(currentUserId);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/{taskId}/claim")
    public ResponseEntity<Void> claimReward(@PathVariable UUID taskId) {
        UUID currentUserId = CurrentUser.get();
        gamificationService.claimReward(currentUserId, taskId);
        return ResponseEntity.ok().build();
    }
}
