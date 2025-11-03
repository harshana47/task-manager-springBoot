package org.example.taskmanager03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
public class UserDTO {
    private UUID userId;
    private String email;
    private String username;
    private String password;
    private String role;
}
