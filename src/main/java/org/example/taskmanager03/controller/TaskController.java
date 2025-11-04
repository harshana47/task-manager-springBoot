package org.example.taskmanager03.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager03.dto.TaskDTO;
import org.example.taskmanager03.service.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Task management endpoints")
public class TaskController {

    private final TaskService taskService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new task",
            responses = {@ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class)))})
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.createTask(taskDTO));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/{uuid}")
    @Operation(summary = "Get task by id",
            responses = {@ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class)))})
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(taskService.getTaskById(uuid));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping("/{uuid}")
    @Operation(summary = "Update task",
            responses = {@ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class)))})
    public ResponseEntity<TaskDTO> updateTask(@PathVariable UUID uuid, @RequestBody TaskDTO taskDTO) {
        return ResponseEntity.ok(taskService.updateTask(uuid, taskDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete task", responses = {@ApiResponse(responseCode = "200", description = "Deleted")})
    public ResponseEntity<String> deleteTask(@PathVariable UUID uuid) {
        taskService.deleteTask(uuid);
        return ResponseEntity.ok("Task deleted successfully");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{taskId}/assign/{userId}")
    @Operation(summary = "Assign task to a user",
            responses = {@ApiResponse(responseCode = "200", description = "Assigned",
                    content = @Content(schema = @Schema(implementation = TaskDTO.class)))})
    public ResponseEntity<TaskDTO> assignTaskToUser(@PathVariable UUID taskId, @PathVariable UUID userId) {
        return ResponseEntity.ok(taskService.assignTaskToUser(taskId, userId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping
    @Operation(summary = "List tasks with optional filters and pagination",
            parameters = {
                    @Parameter(name = "page", description = "Page number (0-based)"),
                    @Parameter(name = "size", description = "Page size"),
                    @Parameter(name = "status", description = "Filter by status"),
                    @Parameter(name = "priority", description = "Filter by priority"),
                    @Parameter(name = "dueDate", description = "Filter by due date (YYYY-MM-DD)"),
                    @Parameter(name = "sortBy", description = "Sort by field")
            },
            responses = {@ApiResponse(responseCode = "200", description = "OK")})
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