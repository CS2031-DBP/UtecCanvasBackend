package com.example.gestiondecursos.Assignment.Dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AssignmentSubmissionDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentLastname;
    private Long assignmentId;
    private String assignmentTitle;
    private Long courseId;
    private String submissionText;
    private String fileUrl;
    private Double score;
    private String feedback;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime gradedAt;
} 