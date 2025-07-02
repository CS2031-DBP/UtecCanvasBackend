package com.example.gestiondecursos.Assignment.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentRequestDTO {
    @NotNull
    private String title;
    
    private String description;
    
    private Integer maxScore;
    
    private LocalDateTime dueDate;
    
    private String material;
    
    private Boolean uploadRequired;
}
