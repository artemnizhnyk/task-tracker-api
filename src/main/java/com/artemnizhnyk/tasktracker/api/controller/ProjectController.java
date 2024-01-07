package com.artemnizhnyk.tasktracker.api.controller;

import com.artemnizhnyk.tasktracker.entity.ProjectEntity;
import com.artemnizhnyk.tasktracker.exception.BadRequestException;
import com.artemnizhnyk.tasktracker.exception.NotFoundException;
import com.artemnizhnyk.tasktracker.repository.ProjectRepository;
import com.artemnizhnyk.tasktracker.service.dto.AnswerDto;
import com.artemnizhnyk.tasktracker.service.dto.ProjectDto;
import com.artemnizhnyk.tasktracker.service.mapper.ProjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping("/api")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;


    public static final String FETCH_PROJECTS = "/projects";
    public static final String CREATE_PROJECT = "/projects";
    public static final String EDIT_PROJECT = "/projects/{project_id}";
    public static final String DELETE_PROJECT = "/projects/{project_id}";
    public static final String CREATE_OR_UPDATE_PROJECT = "/projects";

    @GetMapping(FETCH_PROJECTS)
    public List<ProjectDto> fetchProjects(
            @RequestParam(name = "prefix_name", required = false) Optional<String> optionalPrefixName) {

        optionalPrefixName = optionalPrefixName.filter(prefixName -> !prefixName.trim().isEmpty());

        Stream<ProjectEntity> projectStream = optionalPrefixName
                .map(projectRepository::streamAllByNameStartingWith)
                .orElseGet(() -> projectRepository.findAll().stream());

        return projectStream
                .map(projectMapper::toDto)
                .toList();
    }

    @PostMapping(CREATE_PROJECT)
    public ProjectDto createProject(@RequestParam("project_name") final String projectName) {

        projectRepository
                .findByName(projectName)
                .ifPresent(project -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
                });

        ProjectEntity project = projectRepository.saveAndFlush(
                ProjectEntity.builder()
                        .name(projectName)
                        .build()
        );

        return projectMapper.toDto(project);
    }

    @PutMapping(CREATE_OR_UPDATE_PROJECT)
    public ProjectDto createOrUpdateProject(
            @RequestParam(value = "project_id", required = false) Optional<Long> optionalProjectId,
            @RequestParam(value = "project_name", required = false) Optional<String> optionalProjectName
    ) {
        optionalProjectName = optionalProjectName.filter(projectName -> !projectName.trim().isEmpty());

        boolean isCreate = !optionalProjectId.isPresent();

        final ProjectEntity project = optionalProjectId
                .map(this::getProjectOrThrowException)
                .orElseGet(() -> ProjectEntity.builder().build());

        if (isCreate && !optionalProjectName.isPresent()) {
            throw new BadRequestException("Project name can't be empty.");
        }

        optionalProjectName
                .ifPresent(projectName -> {

                    projectRepository
                            .findByName(projectName)
                            .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project.getId()))
                            .ifPresent(anotherProject -> {
                                throw new BadRequestException(
                                        String.format("Project \"%s\" already exists.", projectName)
                                );
                            });
                    project.setName(projectName);
                });

        final ProjectEntity savedProject = projectRepository.saveAndFlush(project);

        return projectMapper.toDto(savedProject);
    }

    @PatchMapping(EDIT_PROJECT)
    public ProjectDto editProject(
            @PathVariable("project_id") final Long project_id,
            @RequestParam("project_name") final String projectName) {

        if (projectName.trim().isEmpty()) {
            throw new BadRequestException("Name can't be empty.");
        }

        ProjectEntity project = getProjectOrThrowException(project_id);

        projectRepository
                .findByName(projectName)
                .filter(anotherProject -> !Objects.equals(anotherProject.getId(), project_id))
                .ifPresent(anotherProject -> {
                    throw new BadRequestException(String.format("Project \"%s\" already exists.", projectName));
                });

        project.setName(projectName);

        project = projectRepository.saveAndFlush(project);

        return projectMapper.toDto(project);
    }

    @DeleteMapping(DELETE_PROJECT)
    public AnswerDto deleteProject(@PathVariable("project_id") final Long project_id) {

        getProjectOrThrowException(project_id);

        projectRepository.deleteById(project_id);

        return AnswerDto.makeDefault(true);
    }

    private ProjectEntity getProjectOrThrowException(final Long project_id) {
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
