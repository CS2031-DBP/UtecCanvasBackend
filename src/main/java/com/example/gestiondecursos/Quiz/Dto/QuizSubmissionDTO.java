package com.example.gestiondecursos.Quiz.Dto;

import com.example.gestiondecursos.Question.dto.QuestionAnswerDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;
@Data
public class QuizSubmissionDTO {
    @NotNull
    private Long quizId;
    private List<QuestionAnswerDTO> answers;
}