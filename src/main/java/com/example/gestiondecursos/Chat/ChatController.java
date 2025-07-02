package com.example.gestiondecursos.Chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.Instant;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.send/{courseId}")
    @SendTo("/topic/chat/{courseId}")
    public ChatMessage send(@DestinationVariable Long courseId, ChatMessage message) {
        message.setTimestamp(Instant.now());
        chatService.saveMessage(courseId, message);
        return message;
    }

    @GetMapping("/chat/{courseId}/history")
    public ResponseEntity<List<ChatMessage>> getHistory(@PathVariable Long courseId){
        return ResponseEntity.ok(chatService.getHistory(courseId));
    }
} 