package com.example.gestiondecursos.Question.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionResponseDTO {
    private Long id;
    private String question;
    private List<String> options;
    private Boolean isOpen;
}