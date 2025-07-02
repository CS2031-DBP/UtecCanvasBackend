package com.example.gestiondecursos.Lesson.dto;

import com.example.gestiondecursos.Material.dto.MaterialResponseDTO;
import lombok.Data;

import java.util.List;

@Data
public class LessonResponseDTO{
    private Long id;
    private String title;
    private Integer week;
    private String courseTitle;
    private List<String> materialTitles;
    private List<MaterialResponseDTO> materials;
}
