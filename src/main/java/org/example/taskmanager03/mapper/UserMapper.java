package org.example.taskmanager03.mapper;

import org.example.taskmanager03.dto.UserDTO;
import org.example.taskmanager03.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toDTO(User user);

    @Mapping(target = "tasks", ignore = true)
    User toEntity(UserDTO userDTO);
}
