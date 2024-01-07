package com.artemnizhnyk.tasktracker.service.mapper;

import com.artemnizhnyk.tasktracker.service.dto.TaskStateDto;
import com.artemnizhnyk.tasktracker.entity.TaskStateEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TaskStateMapper {
    public TaskStateDto toDto(final TaskStateEntity entity) {
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<TaskStateDto> toDto(final List<TaskStateEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
