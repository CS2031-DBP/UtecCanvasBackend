package com.example.gestiondecursos.Chat;

import lombok.Data;
import java.time.Instant;

@Data
public class ChatMessage {
    private String from;
    private String content;
    private Instant timestamp;
} 