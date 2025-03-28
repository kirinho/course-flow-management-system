package com.liushukov.courseFlow.emailSender.services;

import com.liushukov.courseFlow.emailSender.dtos.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SendEmailService {
    @Value("${spring.mail.username}")
    private String sender;
    private final Logger logger = LoggerFactory.getLogger(SendEmailService.class);
    private final JavaMailSender mailSender;

    public SendEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendConfirmationEmail(EmailDto emailDto) {
        logger.info("📤 Sending confirmation email to: " + emailDto.receiver());
        sendEmail(emailDto, "Confirm your email");
        logger.info("✅ Email sent!");
    }

    @Async
    public void sendGradeNotificationEmail(EmailDto emailDto) {
        logger.info("📤 Sending grade notification email to: " + emailDto.receiver());
        sendEmail(emailDto, "New Grade Notification");
        logger.info("✅ Email sent!");
    }

    private void sendEmail(EmailDto emailDto, String subject) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(emailDto.message(), true);
            helper.setTo(emailDto.receiver());
            helper.setSubject(subject);
            helper.setFrom(sender);
            mailSender.send(mimeMessage);
        } catch (MessagingException ignored) {}
    }
}
