package org.example.taskmanager03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AuthResponse", description = "JWT token and basic user info returned after successful login")
public class AuthResponse {
    @Schema(description = "JWT bearer token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    private String username;
    private String email;
    @Schema(example = "ROLE_ADMIN")
    private String role;
}
