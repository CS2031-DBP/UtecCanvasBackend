package com.example.gestiondecursos.events.userRegister;

import com.example.gestiondecursos.Email.EmailService;
import com.example.gestiondecursos.exceptions.ErrorSendEmailException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRegisterEventListener {
    private final EmailService emailService;

    @EventListener
    @Async
    public void handleUserRegisterEvent(UserRegisterEvent userRegisterEvent){
        try {
            emailService.userRegister(userRegisterEvent.getEmail(), userRegisterEvent.getName(),userRegisterEvent.getLastname(), userRegisterEvent.getEmail(), userRegisterEvent.getPassword());
        } catch (MessagingException e){
            throw new ErrorSendEmailException("Fail to send email to " + userRegisterEvent.getEmail());
        }
    }
}
