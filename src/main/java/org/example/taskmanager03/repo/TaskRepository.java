package org.example.taskmanager03.repo;

import org.example.taskmanager03.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Optional<Task> findByTaskId(UUID uuid);

    // 🔍 Filter tasks by status, priority, or due date (any of them optional)
    @Query("""
           SELECT t FROM Task t
           WHERE (:status IS NULL OR t.status = :status)
             AND (:priority IS NULL OR t.priority = :priority)
             AND (:dueDate IS NULL OR t.dueDate = :dueDate)
           """)
    Page<Task> filterTasks(String status, String priority, LocalDate dueDate, Pageable pageable);

    // 🧍 Find tasks assigned to a specific user
    Page<Task> findByAssignedUserUserId(UUID userUuid, Pageable pageable);
}
