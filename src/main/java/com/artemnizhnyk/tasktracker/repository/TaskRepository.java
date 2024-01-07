package com.artemnizhnyk.tasktracker.repository;

import com.artemnizhnyk.tasktracker.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
