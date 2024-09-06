package com.liushukov.courseFlow.emailSender.dtos;

public record EmailDto(
        String receiver,
        String message
) {
}
