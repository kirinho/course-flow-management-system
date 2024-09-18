package com.liushukov.courseFlow.exceptions;


import java.time.Instant;

public class ErrorDetails {
    private Instant timestamp;
    private String message;
    private String details;

    public ErrorDetails(Instant timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
    public ErrorDetails(){}

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
}
