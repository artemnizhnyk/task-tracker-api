package com.artemnizhnyk.tasktracker.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskStateDto {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    @NonNull
    @JsonProperty("left_task_state_id")
    private Long leftTaskStateId;
    @NonNull
    @JsonProperty("right_task_state_id")
    private Long rightTaskStateId;
    @NonNull
    private String description;
    @NonNull
    @JsonProperty("created_at")
    private Instant createdAt;
    @NonNull
    private List<TaskDto> task;
}
