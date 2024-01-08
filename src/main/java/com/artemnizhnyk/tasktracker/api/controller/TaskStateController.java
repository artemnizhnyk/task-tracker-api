package com.artemnizhnyk.tasktracker.api.controller;

import com.artemnizhnyk.tasktracker.repository.TaskStateRepository;
import com.artemnizhnyk.tasktracker.service.mapper.TaskStateMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping("/api")
public class TaskStateController {

    private final TaskStateRepository taskStateRepository;
    private final TaskStateMapper taskStateMapper;
}
