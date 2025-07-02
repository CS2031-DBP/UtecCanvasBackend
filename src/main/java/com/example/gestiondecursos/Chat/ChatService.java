package com.example.gestiondecursos.Chat;

import com.example.gestiondecursos.Course.domain.Course;
import com.example.gestiondecursos.Course.infrastructure.CourseRepository;
import com.example.gestiondecursos.exceptions.ResourceNotFound;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatRepository;
    private final CourseRepository courseRepository;

    public ChatMessage saveMessage(Long courseId, ChatMessage dto){
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFound("Course not found"));
        ChatMessageEntity entity = new ChatMessageEntity();
        entity.setSender(dto.getFrom());
        entity.setContent(dto.getContent());
        entity.setTimestamp(dto.getTimestamp());
        entity.setCourse(course);
        chatRepository.save(entity);
        return dto;
    }

    public List<ChatMessage> getHistory(Long courseId){
        return chatRepository.findByCourseIdOrderByTimestampAsc(courseId)
                .stream()
                .map(entity -> {
                    ChatMessage dto = new ChatMessage();
                    dto.setFrom(entity.getSender());
                    dto.setContent(entity.getContent());
                    dto.setTimestamp(entity.getTimestamp());
                    return dto;
                })
                .toList();
    }
} 