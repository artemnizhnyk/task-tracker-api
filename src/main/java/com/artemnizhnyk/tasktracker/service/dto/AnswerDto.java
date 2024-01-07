package com.artemnizhnyk.tasktracker.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AnswerDto {

    private Boolean answer;

    public static AnswerDto makeDefault(final Boolean answer) {
        return builder()
                .answer(answer)
                .build();
    }
}
