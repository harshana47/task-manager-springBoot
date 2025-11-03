package org.example.taskmanager03.mapper;

import org.example.taskmanager03.dto.TaskDTO;
import org.example.taskmanager03.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    // Map Entity → DTO
    @Mapping(target = "assignedUserId", source = "assignedUser.userId")
    TaskDTO toDTO(Task task);

    // Map DTO → Entity
    @Mapping(target = "assignedUser", ignore = true) // will be set manually in service
    Task toEntity(TaskDTO taskDTO);
}
