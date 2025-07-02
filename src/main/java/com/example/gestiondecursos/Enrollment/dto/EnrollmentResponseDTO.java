package com.example.gestiondecursos.Enrollment.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EnrollmentResponseDTO {
    private Long id;
    private LocalDateTime enrolled;
    private CourseInfoDTO course;
    private StudentInfoDTO student;
    
    @Data
    public static class CourseInfoDTO {
        private Long id;
        private String title;
        private String description;
        private String section;
        private String category;
    }
    
    @Data
    public static class StudentInfoDTO {
        private Long id;
        private String name;
        private String lastname;
        private String email;
    }
} 