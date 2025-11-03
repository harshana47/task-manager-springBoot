package org.example.taskmanager03.service;

import org.example.taskmanager03.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public interface UserService {
    UserDTO createUser(UserDTO userDTO);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(UUID uuid);

    UserDTO updateUser(UUID uuid, UserDTO userDTO);

    void deleteUser(UUID uuid);
}

