package com.example.gestiondecursos.Instructor.dto;

import lombok.Data;

@Data
public class InstructorResponseDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String profilePhoto;
    private String role;
}

