package com.liushukov.courseFlow.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liushukov.courseFlow.dtos.EmailDto;
import com.liushukov.courseFlow.models.Grade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GradeEmailNotificationObserver implements GradeObserver {
    @Value("${kafka.grade.topic}")
    private String gradeTopicName;
    private final KafkaTemplate<Object, Object> template;
    private final ObjectMapper objectMapper;

    public GradeEmailNotificationObserver(KafkaTemplate<Object, Object> template, ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onGradeChange(Grade grade, boolean created) {
        try {
            String link = "http://localhost:5173/course/" + grade.getSubmission().getAssignment().getModule().getCourse()
                    .getId() + "/assignment/" + grade.getSubmission().getAssignment().getId() + "/overview";
            String email = buildEmail(grade.getSubmission().getStudent().getFullName(), grade.getManager().getFullName(),
                    grade.getManager().getEmail(), grade.getSubmission().getAssignment().getTitle(), link, created);
            EmailDto emailDto = new EmailDto(grade.getSubmission().getStudent().getEmail(), email);
            this.template.send(gradeTopicName, objectMapper.writeValueAsString(emailDto));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String buildEmail(String studentFullName, String managerFullName, String managerEmail, String assignmentTitle,
                              String assignmentLink, boolean created){
        String action = created ? "created" : "updated";
        return "<div style='font-family: Arial, sans-serif; font-size: 16px; color: #333;'>"
                + "<h2 style='color: #0066cc;'>Grade Notification</h2>"
                + "<p>Dear " + studentFullName + ",</p>"
                + "<p>Your grade for the assignment '<b>" + assignmentTitle + "</b>' has been " + action + ".</p>"
                + "<p>Reviewed by: " + managerFullName + " (" + managerEmail + ")</p>"
                + "<p>You can view the details by clicking the link below:</p>"
                + "<p><a href='" + assignmentLink + "' style='color: #28a745; text-decoration: none;'>"
                + "<b>View Assignment Overview</b></a></p>"
                + "<p>If you have any questions, please contact support.</p>"
                + "<p>Best regards,<br>Your Course Management System Team</p>"
                + "</div>";
    }
}
