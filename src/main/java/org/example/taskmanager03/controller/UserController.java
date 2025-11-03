package org.example.taskmanager03.controller;

import lombok.RequiredArgsConstructor;
import org.example.taskmanager03.dto.UserDTO;
import org.example.taskmanager03.entity.User;
import org.example.taskmanager03.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{uuid}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID uuid) {
        return ResponseEntity.ok(userService.getUserById(uuid));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{uuid}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID uuid, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.updateUser(uuid, userDTO));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{uuid}")
    public ResponseEntity<String> deleteUser(@PathVariable UUID uuid) {
        userService.deleteUser(uuid);
        return ResponseEntity.ok("User deleted successfully");
    }
}
