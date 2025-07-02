package com.example.gestiondecursos.Auth.dto;

import lombok.Data;

@Data
public class JwtAuthResponse {
    private String token;
    private UserInfo user;
    
    @Data
    public static class UserInfo {
        private Long id;
        private String email;
        private String name;
        private String lastname;
        private String role;
    }
}

