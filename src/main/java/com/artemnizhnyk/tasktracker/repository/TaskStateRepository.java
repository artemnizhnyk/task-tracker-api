package com.artemnizhnyk.tasktracker.repository;

import com.artemnizhnyk.tasktracker.entity.TaskStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskStateRepository extends JpaRepository<TaskStateEntity, Long> {
}
