package com.example.gestiondecursos.Lesson.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LessonRequestDTO {
    @NotNull
    private String title;
    @NotNull
    @Min(value = 1, message = "Week can't be less than 1")
    @Max(value = 18, message = "Week can't be higher than 18")
    private Integer week;
    //private Long courseId;
}
