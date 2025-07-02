package com.example.gestiondecursos.Quiz.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class QuizSubmissionResponseDTO {
    private Long id;
    private Long quizId;
    private Long studentId;
    private Double automaticScore; // Score for multiple choice questions
    private Double manualScore; // Score for open questions (set by instructor)
    private Double finalScore; // Final score (set by instructor)
    private String status; // "SUBMITTED", "GRADED"
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
}
