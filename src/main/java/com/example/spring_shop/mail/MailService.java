package com.example.spring_shop.mail;


import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Async
public class MailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String token){
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject("Подтверждение");
        mailMessage.setText("Для подтверждения перейдите по ссылке: " +
                "http://localhost:8080/api/v1/auth/confirm?token=" + token);

        mailSender.send(mailMessage);
    }
}
