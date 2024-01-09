package com.artemnizhnyk.tasktracker.api.controller.helpers;

import com.artemnizhnyk.tasktracker.entity.ProjectEntity;
import com.artemnizhnyk.tasktracker.exception.NotFoundException;
import com.artemnizhnyk.tasktracker.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Transactional
public class ControllerHelper {

    private final ProjectRepository projectRepository;

    public ProjectEntity getProjectOrThrowException(final Long project_id) {
        return projectRepository
                .findById(project_id)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format(
                                        "Project with \"%s\" doesn't exist.", project_id
                                )
                        )
                );
    }
}
