package com.artemnizhnyk.tasktracker.exception;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorDto {

    private String error;
    @JsonProperty("error_description")
    private String errorDescription;
}
