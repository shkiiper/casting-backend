package com.casting.platform.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void send(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("✅ Email sent to: {}", to);
        } catch (Exception e) {
            log.error("❌ Email failed to {}: {}", to, e.getMessage());
            // Не бросай exception — транзакция не rollback
        }
    }
    public void sendVerificationCode(String email, String code) {

        String subject = "Подтверждение регистрации";

        String body = """
            Ваш код подтверждения:

            %s

            Код действует 10 минут.
            """.formatted(code);

        send(email, subject, body);
    }
}
