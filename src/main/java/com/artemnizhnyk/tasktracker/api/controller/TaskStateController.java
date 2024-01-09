package com.artemnizhnyk.tasktracker.api.controller;

import com.artemnizhnyk.tasktracker.api.controller.helpers.ControllerHelper;
import com.artemnizhnyk.tasktracker.entity.ProjectEntity;
import com.artemnizhnyk.tasktracker.entity.TaskStateEntity;
import com.artemnizhnyk.tasktracker.exception.BadRequestException;
import com.artemnizhnyk.tasktracker.repository.TaskStateRepository;
import com.artemnizhnyk.tasktracker.service.dto.TaskStateDto;
import com.artemnizhnyk.tasktracker.service.mapper.TaskStateMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping("/api")
public class TaskStateController {

    private final TaskStateRepository taskStateRepository;
    private final TaskStateMapper taskStateMapper;
    private final ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/projects/{project_id}/tasks-states";
    //    public static final String ADD_TASK = "/task-states/{task_state_id}/tasks";
    public static final String CREATE_TASK_STATE = "/projects/{project_id}/task-states";
    public static final String CREATE_OR_UPDATE_TASK_STATE = "/projects";
    public static final String DELETE_PROJECT = "/projects/{project_id}";

    @GetMapping(GET_TASK_STATES)
    public List<TaskStateDto> getTaskStates(@PathVariable(name = "project_id") final Long projectId) {

        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        return project.getTaskStates()
                .stream()
                .map(taskStateMapper::toDto)
                .toList();
    }

    @PostMapping(CREATE_TASK_STATE)
    public TaskStateDto createTaskState(
            @PathVariable(name = "project_id") final Long projectId,
            @RequestParam(name = "task_state_name") String taskStateName) {

        if (taskStateName.isBlank()) {
            throw new BadRequestException("Task state name can't be empty");
        }
        ProjectEntity project = controllerHelper.getProjectOrThrowException(projectId);

        project.getTaskStates()
                .stream()
                .map(TaskStateEntity::getName)
                .filter(anotherTaskStateName -> anotherTaskStateName.equalsIgnoreCase(taskStateName))
                .findAny()
                .ifPresent(it -> {
                    throw new BadRequestException(String.format("Task state \"%s\" already exists", taskStateName));
                });

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity.builder()
                        .name(taskStateName)
                        .build());

        taskStateRepository
                .findTaskStateEntityByRightTaskStateIsNullAndProjectId(projectId)
                .ifPresent(anotherTaskState -> {

                    taskState.setLeftTaskState(anotherTaskState);
                    anotherTaskState.setRightTaskState(taskState);

                    taskStateRepository.saveAndFlush(anotherTaskState);
                });

        final TaskStateEntity savedTaskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateMapper.toDto(savedTaskState);
    }
}
