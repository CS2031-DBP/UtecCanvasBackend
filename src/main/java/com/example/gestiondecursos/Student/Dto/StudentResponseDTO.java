package com.example.gestiondecursos.Student.Dto;

import lombok.Data;

@Data
public class StudentResponseDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private String profilePhoto;
    private String role;
}
