package com.example.gestiondecursos.events.AnnouncementCreated;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.List;

@Getter
public class AnnouncementCreatedEvent extends ApplicationEvent {
    private final List<String> recipients;
    private final String instructorName;
    private final String instructorLastname;
    private final String courseTitle;
    private final String announcementTitle;
    private final String announcementMessage;

    public AnnouncementCreatedEvent(
            Object source,
            List<String> recipients,
            String instructorName,
            String instructorLastname,
            String courseTitle,
            String announcementTitle,
            String announcementMessage) {
        super(source);
        this.recipients = recipients;
        this.instructorName = instructorName;
        this.instructorLastname = instructorLastname;
        this.courseTitle = courseTitle;
        this.announcementTitle = announcementTitle;
        this.announcementMessage = announcementMessage;
    }
}

