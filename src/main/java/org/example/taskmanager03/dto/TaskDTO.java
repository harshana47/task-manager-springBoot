package org.example.taskmanager03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
public class TaskDTO {
    private UUID taskId;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDate dueDate;
    private UUID assignedUserId;
}
