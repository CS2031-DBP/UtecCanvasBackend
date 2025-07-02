package com.example.gestiondecursos.Student.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StudentCreateDTO {
    @NotNull(message = "Name can´t be null")
    private String name;
    @NotNull(message = "Lastname can´t be null")
    private String lastname;
    @NotNull(message = "Email can´t be null")
    private String email;
    @NotNull(message = "Password can´t be null")
    private String password;
}
