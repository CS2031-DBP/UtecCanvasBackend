package com.example.gestiondecursos.Course.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CourseRequestDTO {
    @NotNull(message = "Title can't be null")
    private String title;
    @NotNull(message = "Description can't be null")
    private String description;
    @NotNull(message = "Section can't be null")
    private String section;
    @NotNull(message = "Category can't be null")
    private String category;
}
