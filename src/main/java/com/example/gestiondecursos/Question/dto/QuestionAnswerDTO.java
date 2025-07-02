package com.example.gestiondecursos.Question.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionAnswerDTO {
    @NotNull
    private Long questionId;

    private String answer;
}