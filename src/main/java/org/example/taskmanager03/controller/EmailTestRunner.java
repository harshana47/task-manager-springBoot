package org.example.taskmanager03.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailTestRunner implements CommandLineRunner {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:timberntaste@gmail.com}")
    private String from;

    @Override
    public void run(String... args) throws Exception {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("youremail@gmail.com");
        msg.setFrom(from);
        msg.setSubject("Manual Test Email");
        msg.setText("This is a test email from Task Manager.");

        mailSender.send(msg);
        System.out.println("✅ Test email sent successfully!");
    }
}
