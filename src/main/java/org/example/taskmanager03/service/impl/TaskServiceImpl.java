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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!isAdmin() && !isOwnedByCurrentUser(task)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to access this task");
        }
        return taskMapper.toDTO(task);
    }

    @Override
    public TaskDTO updateTask(UUID uuid, TaskDTO taskDTO) {
        Task task = taskRepository.findByTaskId(uuid)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        if (!isAdmin() && !isOwnedByCurrentUser(task)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to modify this task");
        }
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

        Page<Task> tasks;
        if (isAdmin()) {
            tasks = taskRepository.findAll(pageable);
            if (status != null || priority != null || dueDate != null) {
                LocalDate date = (dueDate != null) ? LocalDate.parse(dueDate) : null;
                tasks = taskRepository.filterTasks(status, priority, date, pageable);
            }
        } else {
            // USER: only own tasks
            String email = currentEmail();
            if (email == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No current user");
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
            tasks = taskRepository.findByAssignedUserUserId(user.getUserId(), pageable);
        }

        return tasks.map(taskMapper::toDTO);
    }

    private boolean isOwnedByCurrentUser(Task task) {
        if (task.getAssignedUser() == null || task.getAssignedUser().getEmail() == null) return false;
        String email = currentEmail();
        return email != null && email.equalsIgnoreCase(task.getAssignedUser().getEmail());
    }

    private boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    private String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : null;
    }
}
