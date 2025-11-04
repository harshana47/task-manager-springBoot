package org.example.taskmanager03.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "AuthRequest", description = "Credentials used to authenticate and obtain a JWT token")
public class AuthRequest {
    @Schema(example = "admin@example.com")
    private String email;
    @Schema(example = "P@ssw0rd")
    private String password;
}
