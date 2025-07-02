package com.example.gestiondecursos.Announcement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnnouncementResponseDTO {
    private String title;
    private String message;
    private String instructorName;
    private String instructorLastname;
    private LocalDateTime createdAt;
}