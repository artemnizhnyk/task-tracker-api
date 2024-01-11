package com.artemnizhnyk.tasktracker.api.controller;

import com.artemnizhnyk.tasktracker.api.controller.helpers.ControllerHelper;
import com.artemnizhnyk.tasktracker.entity.ProjectEntity;
import com.artemnizhnyk.tasktracker.entity.TaskStateEntity;
import com.artemnizhnyk.tasktracker.exception.BadRequestException;
import com.artemnizhnyk.tasktracker.exception.NotFoundException;
import com.artemnizhnyk.tasktracker.repository.TaskStateRepository;
import com.artemnizhnyk.tasktracker.service.dto.AnswerDto;
import com.artemnizhnyk.tasktracker.service.dto.TaskStateDto;
import com.artemnizhnyk.tasktracker.service.mapper.TaskStateMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping("/api")
public class TaskStateController {

    private final TaskStateRepository taskStateRepository;
    private final TaskStateMapper taskStateMapper;
    private final ControllerHelper controllerHelper;

    public static final String GET_TASK_STATES = "/projects/{project_id}/tasks-states";
    public static final String CREATE_TASK_STATE = "/projects/{project_id}/task-states";
    public static final String UPDATE_TASK_STATE = "/task-states/{task_state_id}";
    public static final String CHANGE_TASK_STATE_POSITION = "/task-states/{task_state_id}/position/change";
    public static final String DELETE_TASK_STATE = "/task-states/{task_state_id}";

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

        Optional<TaskStateEntity> optionalAnotherTaskState = Optional.empty();
        for (TaskStateEntity taskState : project.getTaskStates()) {
            if (taskState.getName().equalsIgnoreCase(taskStateName)) {
                throw new BadRequestException(String.format("Task state \"%s\" already exists", taskStateName));
            }
            if (!taskState.getRightTaskState().isPresent()) {
                optionalAnotherTaskState = Optional.of(taskState);
                break;
            }
        }

        TaskStateEntity taskState = taskStateRepository.saveAndFlush(
                TaskStateEntity.builder()
                        .name(taskStateName)
                        .project(project)
                        .build());

        optionalAnotherTaskState.ifPresent(anotherTaskState -> {

            taskState.setLeftTaskState(anotherTaskState);
            anotherTaskState.setRightTaskState(taskState);

            taskStateRepository.saveAndFlush(anotherTaskState);
        });

        final TaskStateEntity savedTaskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateMapper.toDto(savedTaskState);
    }

    @PatchMapping(UPDATE_TASK_STATE)
    public TaskStateDto updateTaskState(
            @PathVariable(name = "task_state_id") final Long taskStateId,
            @RequestParam(name = "task_state_name") String taskStateName) {

        if (taskStateName.isBlank()) {
            throw new BadRequestException("Task state name can't be empty");
        }

        TaskStateEntity taskState = getTaskStateOrThrowException(taskStateId);

        taskStateRepository
                .findTaskStateEntityByProjectIdAndNameContainsIgnoreCase(
                        taskState.getProject().getId()
                        , taskStateName)
                .filter(anotherTaskState -> !anotherTaskState.getId().equals(taskStateId))
                .ifPresent(anotherTaskState -> {
                    throw new BadRequestException(String.format("Task state \"%s\" already exist.", taskStateName));
                });

        taskState.setName(taskStateName);
        taskState = taskStateRepository.saveAndFlush(taskState);

        return taskStateMapper.toDto(taskState);
    }

    @PatchMapping(CHANGE_TASK_STATE_POSITION)
    public TaskStateDto changeTaskStatePosition(
            @PathVariable(name = "task_state_id") final Long taskStateId,
            @RequestParam(name = "left_task_state_id", required = false) Optional<Long> optionalLeftTaskStateId) {

        TaskStateEntity changingTaskState = getTaskStateOrThrowException(taskStateId);

        ProjectEntity project = changingTaskState.getProject();

        Optional<Long> optionalOldTaskStateId = changingTaskState
                .getLeftTaskState()
                .map(TaskStateEntity::getId);

        if (optionalOldTaskStateId.equals(optionalLeftTaskStateId)) {
            return taskStateMapper.toDto(changingTaskState);
        }

        Optional<TaskStateEntity> optionalNewLeftTaskState = optionalLeftTaskStateId
                .map(leftTaskStateId -> {

                    if (taskStateId.equals(leftTaskStateId)) {
                        throw new BadRequestException("Left task state id can't be equals with changing task state id");
                    }

                    TaskStateEntity leftTaskStateEntity = getTaskStateOrThrowException(leftTaskStateId);

                    if (!project.getId().equals(leftTaskStateEntity.getProject().getId())) {
                        throw new BadRequestException("Task state position can be changed only within the same project");
                    }

                    return leftTaskStateEntity;
                });

        Optional<TaskStateEntity> optionalNewRightTaskState;

        if (optionalNewLeftTaskState.isEmpty()) {
            optionalNewRightTaskState = project
                    .getTaskStates()
                    .stream()
                    .filter(anotherTaskState -> anotherTaskState.getLeftTaskState().isEmpty())
                    .findAny();
        } else {
            optionalNewRightTaskState = optionalNewLeftTaskState
                    .get()
                    .getRightTaskState();
        }

        replaceOldTaskStatePosition(changingTaskState);

        if (optionalNewLeftTaskState.isPresent()) {

            TaskStateEntity newLeftTaskState = optionalNewLeftTaskState.get();

            newLeftTaskState.setRightTaskState(changingTaskState);

            changingTaskState.setLeftTaskState(newLeftTaskState);

        } else {
            changingTaskState.setLeftTaskState(null);
        }

        if (optionalNewRightTaskState.isPresent()) {

            TaskStateEntity newRightTaskState = optionalNewRightTaskState.get();

            newRightTaskState.setLeftTaskState(changingTaskState);

            changingTaskState.setRightTaskState(newRightTaskState);

        } else {
            changingTaskState.setRightTaskState(null);
        }

        changingTaskState = taskStateRepository.saveAndFlush(changingTaskState);

        optionalNewRightTaskState
                .ifPresent(taskStateRepository::saveAndFlush);
        optionalNewLeftTaskState
                .ifPresent(taskStateRepository::saveAndFlush);

        return taskStateMapper.toDto(changingTaskState);
    }

    @DeleteMapping(DELETE_TASK_STATE)
    public AnswerDto deleteTaskState(@PathVariable(name = "task_state_id") final Long taskStateId) {

        TaskStateEntity changeTaskState = getTaskStateOrThrowException(taskStateId);

        replaceOldTaskStatePosition(changeTaskState);

        taskStateRepository.delete(changeTaskState);

        return AnswerDto.builder().answer(true).build();
    }

    private void replaceOldTaskStatePosition(final TaskStateEntity changingTaskState) {

        Optional<TaskStateEntity> optionalOldLeftTaskState = changingTaskState.getLeftTaskState();
        Optional<TaskStateEntity> optionalOldRightTaskState = changingTaskState.getRightTaskState();

        optionalOldLeftTaskState
                .ifPresent(it -> {

                    it.setRightTaskState(optionalOldRightTaskState.orElse(null));

                    taskStateRepository.saveAndFlush(it);
                });

        optionalOldRightTaskState
                .ifPresent(it -> {

                    it.setLeftTaskState(optionalOldLeftTaskState.orElse(null));

                    taskStateRepository.saveAndFlush(it);
                });
    }

    private TaskStateEntity getTaskStateOrThrowException(final Long taskStateId) {
        return taskStateRepository
                .findById(taskStateId)
                .orElseThrow(() ->
                        new NotFoundException(
                                String.format("Task state with \"%s\" id doesn't exist.", taskStateId)
                        )
                );
    }
}
