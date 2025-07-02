package com.example.gestiondecursos.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;


    @Async
    public void assignmentCreated(String to, String name, String lastname, String title, Integer maxScore, String dueDate) throws MessagingException {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("lastname", lastname);
        context.setVariable("title", title);
        context.setVariable("maxScore", maxScore);
        context.setVariable("dueDate", dueDate);

        String process = templateEngine.process("CORREO.HTML", context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setText(process, true);
        helper.setSubject("Assignment Created");
        javaMailSender.send(message);
    }

    @Async
    public void userRegister(String to, String name, String lastname, String email, String password) throws MessagingException{
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("lastname", lastname);
        context.setVariable("email", email);
        context.setVariable("password", password);

        String process = templateEngine.process("SignInEmail.html", context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        helper.setTo(to);
        helper.setText(process, true);
        helper.setSubject("Bienvenido a UTEC++");
        javaMailSender.send(message);
    }

    @Async
    public void announcementCreated(
            List<String> to,
            String instructorName,
            String instructorLastname,
            String courseTitle,
            String announcementTitle,
            String announcementMessage) {

        for (String recipient : to) {
            try {
                sendAnnouncementEmail(
                        recipient,
                        instructorName,
                        instructorLastname,
                        courseTitle,
                        announcementTitle,
                        announcementMessage
                );
            } catch (MessagingException e) {
                throw new RuntimeException("Error sending email to: " + recipient, e);
            }
        }
    }

    public void sendAnnouncementEmail(
            String to,
            String instructorName,
            String instructorLastname,
            String courseTitle,
            String announcementTitle,
            String announcementMessage) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        Context context = new Context();
        context.setVariable("instructorName", instructorName);
        context.setVariable("instructorLastname", instructorLastname);
        context.setVariable("courseTitle", courseTitle);
        context.setVariable("announcementTitle", announcementTitle);
        context.setVariable("announcementMessage", announcementMessage);

        String html = templateEngine.process("announcement-template", context);

        helper.setTo(to);
        helper.setSubject("Nuevo anuncio: " + announcementTitle);
        helper.setText(html, true);

        javaMailSender.send(message);
    }



}
