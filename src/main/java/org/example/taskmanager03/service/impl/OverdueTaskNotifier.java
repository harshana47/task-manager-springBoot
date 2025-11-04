package org.example.taskmanager03.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.taskmanager03.entity.Task;
import org.example.taskmanager03.repo.TaskRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "features.overdue-email.enabled", havingValue = "true")
public class OverdueTaskNotifier {

    private final TaskRepository taskRepository;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.username:task-manager@example.org}")
    private String fromAddress;

    @Scheduled(cron = "0 */1 * * * *")
    public void notifyOverdueTasks() {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Overdue email feature enabled but no JavaMailSender configured. Skipping run.");
            return;
        }
        LocalDate today = LocalDate.now();
        List<Task> overdue = taskRepository.findOverdueTasks(today);
        if (overdue.isEmpty()) {
            log.info("No overdue tasks found for {}", today);
            return;
        }
        int sent = 0;
        for (Task t : overdue) {
            if (t.getAssignedUser() == null || t.getAssignedUser().getEmail() == null) {
                log.warn("Task {} has no assigned user/email; skipping notification", t.getTaskId());
                continue;
            }
            try {
                sendEmail(mailSender, t);
                sent++;
            } catch (Exception ex) {
                log.error("Failed to send overdue email for task {} to {}: {}",
                        t.getTaskId(), t.getAssignedUser().getEmail(), ex.getMessage());
            }
        }
        log.info("Overdue email run completed. Total tasks: {}, Emails sent: {}", overdue.size(), sent);
    }

    private void sendEmail(JavaMailSender mailSender, Task t) {
        String to = t.getAssignedUser().getEmail();
        String subject = "Task overdue: " + nullSafe(t.getTitle());
        String body = String.format(
                "Hello %s,\n\nThe task '%s' is overdue.\nDue date: %s\nPriority: %s\nStatus: %s\n\nPlease take the necessary action.\n\n— Task Manager",
                nullSafe(t.getAssignedUser().getUsername()),
                nullSafe(t.getTitle()),
                t.getDueDate(),
                nullSafe(t.getPriority()),
                nullSafe(t.getStatus())
        );
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(fromAddress);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        mailSender.send(msg);
        log.debug("Sent overdue email for task {} to {}", t.getTaskId(), to);
    }

    private String nullSafe(String s) { return s == null ? "(unknown)" : s; }
}
