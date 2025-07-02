package com.example.gestiondecursos.Question.dto;

import com.example.gestiondecursos.Question.domain.QuestionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class QuestionRequestDTO {
    @NotNull
    private String question;

    private String correctAnswer;
    private List<String> options;
    @NotNull
    private QuestionType questionType;
    private Boolean isOpen;
}
