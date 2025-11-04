package org.example.taskmanager03.repo;

import org.example.taskmanager03.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    Optional<Task> findByTaskId(UUID uuid);

    @Query("""
           SELECT t FROM Task t
           WHERE (:status IS NULL OR t.status = :status)
             AND (:priority IS NULL OR t.priority = :priority)
             AND (:dueDate IS NULL OR t.dueDate = :dueDate)
           """)
    Page<Task> filterTasks(String status, String priority, LocalDate dueDate, Pageable pageable);

    Page<Task> findByAssignedUserUserId(UUID userUuid, Pageable pageable);

    @Query("SELECT t FROM Task t WHERE t.dueDate <= :date AND t.status <> 'DONE'")
    List<Task> findOverdueTasks(LocalDate date);
}
