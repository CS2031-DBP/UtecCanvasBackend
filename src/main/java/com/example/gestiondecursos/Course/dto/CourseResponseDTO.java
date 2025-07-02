package com.example.gestiondecursos.Course.dto;

import lombok.Data;

@Data
public class CourseResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String category;
    private String section;
    private String instructorName;
    private String instructorLastname;
    private String instructorEmail;
    private String instructorProfilePhoto;
    private Integer lessonCount;
    private Integer assignmentCount;
    private Integer quizCount;
}
