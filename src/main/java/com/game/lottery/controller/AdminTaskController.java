package com.game.lottery.controller;

import com.game.lottery.dto.TaskDto;
import com.game.lottery.exception.TaskNotFoundException;
import com.game.lottery.mapper.TaskMapper;
import com.game.lottery.model.Task;
import com.game.lottery.repository.TaskRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminTaskController {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @GetMapping
    public ResponseEntity<List<TaskDto>> getAllTasks() {
        List<TaskDto> tasks = taskRepository.findAll().stream()
                .map(taskMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(tasks);
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@RequestBody @Valid TaskDto request) {
        Task task = taskMapper.toEntity(request);
        task.setId(null); // Force new creation
        task = taskRepository.save(task);
        return ResponseEntity.created(URI.create("/api/v1/admin/tasks/" + task.getId()))
                .body(taskMapper.toDto(task));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable UUID id, @RequestBody @Valid TaskDto request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        taskMapper.updateEntityFromDto(request, task);
        task = taskRepository.save(task);

        return ResponseEntity.ok(taskMapper.toDto(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));

        task.setActive(false); // Soft delete
        taskRepository.save(task);

        return ResponseEntity.noContent().build();
    }
}
