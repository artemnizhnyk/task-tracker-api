package com.artemnizhnyk.tasktracker.api.mapper;

import com.artemnizhnyk.tasktracker.api.dto.TaskStateDto;
import com.artemnizhnyk.tasktracker.entity.TaskStateEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class TaskStateMapper {
    public TaskStateDto toDto(TaskStateEntity entity) {
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public List<TaskStateDto> toDto(List<TaskStateEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
