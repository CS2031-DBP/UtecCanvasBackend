package com.example.gestiondecursos.Material.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MaterialRequestDTO {
    @NotNull(message = "Title can´t be null")
    private String title;
    @NotNull(message = "Type can´t be null")
    private String type;
    private String url;
}
