package org.example.taskmanager03.service;

import org.example.taskmanager03.dto.TaskDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface TaskService {
    TaskDTO createTask(TaskDTO taskDTO);

    TaskDTO getTaskById(UUID uuid);

    TaskDTO updateTask(UUID uuid, TaskDTO taskDTO);

    void deleteTask(UUID uuid);

    TaskDTO assignTaskToUser(UUID taskId, UUID userId);

    Page<TaskDTO> getTasks(int page, int size, String status, String priority, String dueDate, String sortBy);
}