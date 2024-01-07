package com.artemnizhnyk.tasktracker.service.mapper;

import com.artemnizhnyk.tasktracker.service.dto.TaskDto;
import com.artemnizhnyk.tasktracker.entity.TaskEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskMapper {
    public TaskDto toDto(final TaskEntity entity) {
        return TaskDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<TaskDto> toDto(final List<TaskEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
