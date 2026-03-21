package com.casting.platform.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    public void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
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
            <div style="margin:0;padding:24px;background:#f4f6fb;font-family:Arial,sans-serif;color:#1f2937;">
              <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;padding:32px;border:1px solid #e5e7eb;">
                <div style="font-size:24px;font-weight:700;margin-bottom:12px;color:#111827;">Подтверждение регистрации</div>
                <p style="margin:0 0 16px;font-size:16px;line-height:1.6;">
                  Спасибо за регистрацию в Onset Casting. Используйте этот код для подтверждения аккаунта:
                </p>
                <div style="margin:24px 0;padding:18px 20px;background:#f9fafb;border:1px dashed #d1d5db;border-radius:12px;text-align:center;">
                  <span style="font-size:32px;letter-spacing:8px;font-weight:700;color:#111827;">%s</span>
                </div>
                <p style="margin:0 0 12px;font-size:15px;line-height:1.6;">
                  Код действует <strong>10 минут</strong>.
                </p>
                <p style="margin:0;font-size:14px;line-height:1.6;color:#6b7280;">
                  Если вы не создавали аккаунт, просто проигнорируйте это письмо.
                </p>
              </div>
            </div>
            """.formatted(code);

        sendHtml(email, subject, body);
    }

    public void sendPasswordResetEmail(String email, String link) {
        String subject = "Сброс пароля";

        String body = """
            <div style="margin:0;padding:24px;background:#f4f6fb;font-family:Arial,sans-serif;color:#1f2937;">
              <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;padding:32px;border:1px solid #e5e7eb;">
                <div style="font-size:24px;font-weight:700;margin-bottom:12px;color:#111827;">Сброс пароля</div>
                <p style="margin:0 0 16px;font-size:16px;line-height:1.6;">
                  Мы получили запрос на смену пароля. Нажмите на кнопку ниже, чтобы продолжить:
                </p>
                <div style="margin:24px 0;">
                  <a href="%s" style="display:inline-block;padding:14px 22px;background:#111827;color:#ffffff;text-decoration:none;border-radius:10px;font-size:15px;font-weight:700;">
                    Сбросить пароль
                  </a>
                </div>
                <p style="margin:0 0 12px;font-size:14px;line-height:1.6;color:#4b5563;">
                  Если кнопка не открывается, используйте эту ссылку:
                </p>
                <p style="margin:0;font-size:14px;line-height:1.6;word-break:break-all;">
                  <a href="%s" style="color:#2563eb;text-decoration:none;">%s</a>
                </p>
              </div>
            </div>
            """.formatted(link, link, link);

        sendHtml(email, subject, body);
    }

    public void sendMissingPhotoReminderEmail(String email) {
        String subject = "Добавьте фото в профиль";

        String body = """
            <div style="margin:0;padding:24px;background:#f4f6fb;font-family:Arial,sans-serif;color:#1f2937;">
              <div style="max-width:560px;margin:0 auto;background:#ffffff;border-radius:16px;padding:32px;border:1px solid #e5e7eb;">
                <div style="font-size:24px;font-weight:700;margin-bottom:12px;color:#111827;">Добавьте фото в профиль</div>
                <p style="margin:0 0 16px;font-size:16px;line-height:1.6;">
                  В вашем профиле пока нет фотографии.
                </p>
                <p style="margin:0 0 16px;font-size:16px;line-height:1.6;">
                  Чтобы профиль отображался в каталоге и выглядел полноценно для клиентов, пожалуйста, загрузите фото.
                </p>
                <div style="margin:24px 0;padding:18px 20px;background:#f9fafb;border:1px solid #e5e7eb;border-radius:12px;">
                  <p style="margin:0;font-size:14px;line-height:1.6;color:#4b5563;">
                    После добавления фотографии ваш профиль будет лучше представлен в каталоге и станет заметнее для заказчиков.
                  </p>
                </div>
                <p style="margin:0;font-size:14px;line-height:1.6;color:#6b7280;">
                  Если фото уже загружено, просто проигнорируйте это письмо.
                </p>
              </div>
            </div>
            """;

        sendHtml(email, subject, body);
    }
}
