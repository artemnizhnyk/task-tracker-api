package com.artemnizhnyk.tasktracker.api.mapper;

import com.artemnizhnyk.tasktracker.api.dto.ProjectDto;
import com.artemnizhnyk.tasktracker.entity.ProjectEntity;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class ProjectMapper {
    public ProjectDto toDto(ProjectEntity entity) {
        return ProjectDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public List<ProjectDto> toDto(List<ProjectEntity> entities) {
        return entities.stream()
                .map(this::toDto)
                .toList();
    }
}
