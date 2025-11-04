package org.example.taskmanager03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@Schema(name = "Task", description = "Task payload")
public class TaskDTO {
    @Schema(description = "Unique identifier of the task")
    private UUID taskId;
    @Schema(example = "Write unit tests")
    private String title;
    @Schema(example = "Cover service layer with JUnit and Mockito")
    private String description;
    @Schema(example = "HIGH", allowableValues = {"LOW","MEDIUM","HIGH"})
    private String priority;
    @Schema(example = "OPEN", allowableValues = {"OPEN","IN_PROGRESS","DONE"})
    private String status;
    @Schema(example = "2025-11-15")
    private LocalDate dueDate;
    @Schema(description = "Assigned user's ID")
    private UUID assignedUserId;
}
