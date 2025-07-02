package com.example.gestiondecursos.Quiz.Dto;

import com.example.gestiondecursos.Question.dto.QuestionResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class QuizResponseDTO {
    private Long id;
    private String title;
    private String instructions;
    private LocalDateTime dueDate;
    private Double maxScore;
    private Long courseId;
    private List<QuestionResponseDTO> questions;
}