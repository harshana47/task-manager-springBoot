package org.example.taskmanager03.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager03.dto.TaskDTO;
import org.example.taskmanager03.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 🟢 Create task (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createTask(taskDTO));
    }

    // 🟢 Get task by ID (Admin or assigned User)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{uuid}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(taskService.getTaskById(uuid));
    }

    // 🟡 Update task (Admin or assigned User)
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{uuid}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID uuid, @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(uuid, taskDTO));
    }

    // 🔴 Delete task (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteTask(@PathVariable UUID uuid) {
        taskService.deleteTask(uuid);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{taskId}/assign/{userId}")
    public ResponseEntity<TaskDTO> assignTaskToUser(@PathVariable UUID taskId, @PathVariable UUID userId) {
        return ResponseEntity.ok(taskService.assignTaskToUser(taskId, userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    public ResponseEntity<Page<TaskDTO>> getTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String dueDate,
            @RequestParam(defaultValue = "id") String sortBy) {

        return ResponseEntity.ok(taskService.getTasks(page, size, status, priority, dueDate, sortBy));
    }
}
