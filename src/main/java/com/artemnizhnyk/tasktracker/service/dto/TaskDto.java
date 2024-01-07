package com.artemnizhnyk.tasktracker.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class TaskDto {
        @NonNull
        private Long id;
        @NonNull
        private String name;
        @NonNull
        private String description;
        @NonNull
        @JsonProperty("created_at")
        private Instant createdAt;
}
