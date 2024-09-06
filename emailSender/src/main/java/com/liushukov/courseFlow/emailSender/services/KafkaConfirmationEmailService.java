package com.liushukov.courseFlow.emailSender.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liushukov.courseFlow.emailSender.dtos.EmailDto;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConfirmationEmailService {

    private final ObjectMapper objectMapper;
    private final SendEmailService sendEmailService;

    public KafkaConfirmationEmailService(ObjectMapper objectMapper, SendEmailService sendEmailService) {
        this.objectMapper = objectMapper;
        this.sendEmailService = sendEmailService;
    }

    @KafkaListener(id = "listener", topics = "verification")
    public void listen(String message) throws JsonProcessingException {
        var loginDto = objectMapper.readValue(message, EmailDto.class);
        sendEmailService.sendEmail(loginDto);
    }
}
