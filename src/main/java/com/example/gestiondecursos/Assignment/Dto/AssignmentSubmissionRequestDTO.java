package com.example.gestiondecursos.Assignment.Dto;

import lombok.Data;

@Data
public class AssignmentSubmissionRequestDTO {
    private Long assignmentId;
    private String submissionText;
    private String fileUrl;
} 