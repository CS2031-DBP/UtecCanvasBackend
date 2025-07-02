package com.example.gestiondecursos.Course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequestForUpdateDTO {
    private String title;
    private String description;
    private String section;
    private String category;
}
