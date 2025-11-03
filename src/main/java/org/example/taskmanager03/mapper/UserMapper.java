package org.example.taskmanager03.mapper;

import org.example.taskmanager03.dto.UserDTO;
import org.example.taskmanager03.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "uuid", source = "uuid")
    UserDTO toDTO(User user);

    @Mapping(target = "uuid", source = "uuid")
    User toEntity(UserDTO userDTO);
}
