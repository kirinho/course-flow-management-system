package com.liushukov.courseFlow.emailSender.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liushukov.courseFlow.emailSender.dtos.EmailDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConfirmationEmailService {
    private final Logger logger = LoggerFactory.getLogger(KafkaConfirmationEmailService.class);
    private final ObjectMapper objectMapper;
    private final SendEmailService sendEmailService;

    public KafkaConfirmationEmailService(ObjectMapper objectMapper, SendEmailService sendEmailService) {
        this.objectMapper = objectMapper;
        this.sendEmailService = sendEmailService;
    }

    @KafkaListener(id = "verificationListener", topics = "verification")
    public void listenVerification(String message) throws JsonProcessingException {
        logger.info("received message for verification");
        EmailDto emailDto = objectMapper.readValue(message, EmailDto.class);
        sendEmailService.sendConfirmationEmail(emailDto);
    }

    @KafkaListener(id = "gradeNotificationListener", topics = "grade-email-notification")
    public void listenGradeNotification(String message) throws JsonProcessingException {
        logger.info("received message for grade notification");
        EmailDto emailDto = objectMapper.readValue(message, EmailDto.class);
        sendEmailService.sendGradeNotificationEmail(emailDto);
    }
}
