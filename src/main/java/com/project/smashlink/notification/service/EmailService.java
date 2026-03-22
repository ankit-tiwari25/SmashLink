package com.project.smashlink.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@smashlink.com");
            message.setTo("ankit.tiwary0101@gmail.com");
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Sent email to " + toEmail);
        }catch (Exception e) {
            log.error("Failed to send email : " + e.getMessage());
        }
    }

}
