package org.example.taskmanager03.mapper;

import org.example.taskmanager03.dto.TaskDTO;
import org.example.taskmanager03.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "assignedUserId", source = "assignedUser.userId")
    TaskDTO toDTO(Task task);

    @Mapping(target = "assignedUser", ignore = true)
    Task toEntity(TaskDTO taskDTO);
}
