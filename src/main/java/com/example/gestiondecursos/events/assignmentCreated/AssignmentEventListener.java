package com.example.gestiondecursos.events.assignmentCreated;

import com.example.gestiondecursos.Email.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AssignmentEventListener {
    private final EmailService emailService;

    public AssignmentEventListener(EmailService emailService){
        this.emailService = emailService;
    }

//    @EventListener
//    @Async
//    public void handleAssignmentEvent(AssignmentEvent event){
//        try{
//            emailService.assignmentCreated(event.getTitle(), event.getMaxScore(), event.getDueDate());
//        }
//    }
}
