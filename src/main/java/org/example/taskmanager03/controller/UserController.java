package org.example.taskmanager03.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager03.dto.UserDTO;
import org.example.taskmanager03.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @Operation(summary = "Create user",
            responses = {@ApiResponse(responseCode = "200", description = "Created",
                    content = @Content(schema = @Schema(implementation = UserDTO.class)))})
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "List users",
            responses = {@ApiResponse(responseCode = "200", description = "OK")})
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{uuid}")
    @Operation(summary = "Get user by id",
            responses = {@ApiResponse(responseCode = "200", description = "OK",
                    content = @Content(schema = @Schema(implementation = UserDTO.class)))})
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(userService.getUserById(uuid));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{uuid}")
    @Operation(summary = "Update user",
            responses = {@ApiResponse(responseCode = "200", description = "Updated",
                    content = @Content(schema = @Schema(implementation = UserDTO.class)))})
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID uuid, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(uuid, userDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    @Operation(summary = "Delete user",
            responses = {@ApiResponse(responseCode = "200", description = "Deleted")})
    public ResponseEntity<String> deleteUser(@PathVariable UUID uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.ok("User deleted successfully");
    }
}
