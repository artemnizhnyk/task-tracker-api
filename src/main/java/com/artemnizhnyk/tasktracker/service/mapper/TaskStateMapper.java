package com.artemnizhnyk.tasktracker.service.mapper;

import com.artemnizhnyk.tasktracker.service.dto.TaskStateDto;
import com.artemnizhnyk.tasktracker.entity.TaskStateEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskStateMapper {

    private final TaskMapper taskMapper;

    public TaskStateDto toDto(final TaskStateEntity entity) {
        return TaskStateDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .leftTaskStateId(entity.getLeftTaskState().map(TaskStateEntity::getId).orElse(null))
                .rightTaskStateId(entity.getRightTaskState().map(TaskStateEntity::getId).orElse(null))
                .createdAt(entity.getCreatedAt())
                .task(entity.getTasks()
                        .stream()
                        .map(taskMapper::toDto)
                        .toList())
                .build();
    }

    public List<TaskStateDto> toDto(final List<TaskStateEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
