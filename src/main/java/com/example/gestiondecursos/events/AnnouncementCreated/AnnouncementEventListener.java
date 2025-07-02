package com.example.gestiondecursos.events.AnnouncementCreated;

import com.example.gestiondecursos.Email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnnouncementEventListener {
    private final EmailService emailService;

    @EventListener
    @Async
    public void handleAnnouncementCreated(AnnouncementCreatedEvent event) {
        emailService.announcementCreated(
                event.getRecipients(),
                event.getInstructorName(),
                event.getInstructorLastname(),
                event.getCourseTitle(),
                event.getAnnouncementTitle(),
                event.getAnnouncementMessage()
        );
    }
}
