package com.example.gestiondecursos.Assignment.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Integer maxScore;
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private String material;
    private Boolean uploadRequired;
    private Long courseId;
}
