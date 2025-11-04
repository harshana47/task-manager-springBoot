package org.example.taskmanager03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Component
@Schema(name = "User", description = "User payload")
public class UserDTO {
    @Schema(description = "Unique identifier of the user")
    private UUID userId;
    @Schema(example = "user@xample.com")
    private String email;
    @Schema(example = "harshana")
    private String username;
    @Schema(example = "Password")
    private String password;
    @Schema(example = "ROLE_USER", allowableValues = {"ROLE_USER","ROLE_ADMIN"})
    private String role;
}
