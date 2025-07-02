package com.example.gestiondecursos.Quiz.Dto;

import com.example.gestiondecursos.Question.dto.QuestionRequestDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizRequestDTO {
    private String title;
    @Min(value = 0, message = "Value can´t less than 0")
    private Double maxScore;
    private String instructions;
    private LocalDateTime dueDate;
    private List<QuestionRequestDTO> questions;
}
