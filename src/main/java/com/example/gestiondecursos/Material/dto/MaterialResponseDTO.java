package com.example.gestiondecursos.Material.dto;

import lombok.Data;

@Data
public class MaterialResponseDTO {
    private Long id;
    private String title;
    private String type;
    private String url;
    private Long courseId;
    private String lessonTitle;
}
