package com.liushukov.courseFlow.emailSender.controllers;

import com.liushukov.courseFlow.emailSender.dtos.EmailDto;
import com.liushukov.courseFlow.emailSender.services.SendEmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final SendEmailService sendEmailService;

    public EmailController(SendEmailService sendEmailService) {
        this.sendEmailService = sendEmailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody EmailDto emailDto) {
        sendEmailService.sendEmail(emailDto);
        return ResponseEntity.status(HttpStatus.OK).body("View your email, we've just sent verification letter");
    }
}
