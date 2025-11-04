package org.example.taskmanager03.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager03.dto.TaskDTO;
import org.example.taskmanager03.entity.Task;
import org.example.taskmanager03.entity.User;
import org.example.taskmanager03.mapper.TaskMapper;
import org.example.taskmanager03.repo.TaskRepository;
import org.example.taskmanager03.repo.UserRepository;
import org.example.taskmanager03.service.QuoteService;
import org.example.taskmanager03.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Autowired
    private QuoteService quoteService;

    @Override
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = taskMapper.toEntity(taskDTO);

        task.setTaskId(null);

        String quote = quoteService.getMotivationalQuote();
        String baseDesc = task.getDescription();
        if (baseDesc == null || baseDesc.isBlank()) {
            task.setDescription("Motivation: " + quote);
        } else {
            task.setDescription(baseDesc + " | Motivation: " + quote);
        }

        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Override
    public TaskDTO getTaskById(UUID uuid) {
        Task task = taskRepository.findByTaskId(uuid)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return taskMapper.toDTO(task);
    }

    @Override
    public TaskDTO updateTask(UUID uuid, TaskDTO taskDTO) {
        Task task = taskRepository.findByTaskId(uuid)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setStatus(taskDTO.getStatus());
        task.setDueDate(taskDTO.getDueDate());
        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Override
    public void deleteTask(UUID uuid) {
        Task task = taskRepository.findByTaskId(uuid)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    @Override
    public TaskDTO assignTaskToUser(UUID taskId, UUID userId) {
        Task task = taskRepository.findByTaskId(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        task.setAssignedUser(user);
        return taskMapper.toDTO(taskRepository.save(task));
    }

    @Override
    public Page<TaskDTO> getTasks(int page, int size, String status, String priority, String dueDate, String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Page<Task> tasks = taskRepository.findAll(pageable);

        if (status != null || priority != null || dueDate != null) {
            LocalDate date = (dueDate != null) ? LocalDate.parse(dueDate) : null;
            tasks = taskRepository.filterTasks(status, priority, date, pageable);
        }

        return tasks.map(taskMapper::toDTO);
    }
}
