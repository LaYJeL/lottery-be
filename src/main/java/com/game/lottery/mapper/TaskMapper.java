package com.game.lottery.mapper;

import com.game.lottery.dto.TaskDto;
import com.game.lottery.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toDto(Task task);

    Task toEntity(TaskDto dto);

    void updateEntityFromDto(TaskDto dto, @MappingTarget Task task);
}
