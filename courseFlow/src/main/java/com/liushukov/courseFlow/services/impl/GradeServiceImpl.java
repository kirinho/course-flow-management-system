package com.liushukov.courseFlow.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liushukov.courseFlow.dtos.*;
import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.Grade;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.repositories.BaseLessonAssignmentRepository;
import com.liushukov.courseFlow.repositories.EnrollmentRepository;
import com.liushukov.courseFlow.repositories.GradeRepository;
import com.liushukov.courseFlow.services.GradeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeServiceImpl implements GradeService {
    @Value("${kafka.grade.topic}")
    private String gradeTopicName;
    private final GradeRepository gradeRepository;
    private final BaseLessonAssignmentRepository repository;
    private final EnrollmentRepository enrollmentRepository;
    private final KafkaTemplate<Object, Object> template;
    private final ObjectMapper objectMapper;

    public GradeServiceImpl(GradeRepository gradeRepository, BaseLessonAssignmentRepository repository,
                            EnrollmentRepository enrollmentRepository, KafkaTemplate<Object, Object> template,
                            ObjectMapper objectMapper) {
        this.gradeRepository = gradeRepository;
        this.repository = repository;
        this.enrollmentRepository = enrollmentRepository;
        this.template = template;
        this.objectMapper = objectMapper;
    }

    @Override
    public Optional<Grade> getGradeById(long gradeId) {
        return gradeRepository.findGradeById(gradeId);
    }

    @Override
    public List<AssignmentUserGradeResponseDto> getUserAssignmentsAndGrades(long courseId, long studentId) {
        List<Assignment> assignments = repository.findAssignmentsByCourseAndStudent(courseId, studentId);
        return assignments.stream().map(assignment -> {
            Submission submission = assignment.getSubmissions().stream()
                    .filter(s -> s.getStudent().getId().equals(studentId))
                    .findFirst()
                    .orElse(null);
            Integer currentScore = (submission != null && submission.getGrade() != null)
                    ? submission.getGrade().getScore()
                    : null;
            return new AssignmentUserGradeResponseDto(
                    assignment.getId(),
                    assignment.getTitle(),
                    assignment.getDescription(),
                    assignment.getDueDate(),
                    assignment.getMaxScore(),
                    currentScore
            );
        }).toList();
    }

    @Override
    public List<UserGradesResponseDto> getStudentsWithGradesByCourse(long courseId) {
        return enrollmentRepository.findStudentTotalGradesByCourse(courseId)
                .stream()
                .map(value -> new UserGradesResponseDto(
                        value.getStudentId(),
                        value.getFullName(),
                        value.getTotalScore(),
                        value.getMaxTotalScore()
                )).toList();
    }

    @Override
    public void createGrade(GradeCreateDto gradeCreateDto, User manager, Submission submission) throws JsonProcessingException {
        Grade grade = new Grade(gradeCreateDto.score(), gradeCreateDto.feedback(), manager, submission);
        gradeRepository.save(grade);
        sendGradeNotificationEmail(grade, true);
    }

    @Override
    public void updateGrade(Grade grade, GradeUpdateDto gradeUpdateDto) throws JsonProcessingException {
        if (gradeUpdateDto.score() != null) {
            grade.setScore(gradeUpdateDto.score());
        }
        if (gradeUpdateDto.feedback() != null) {
            grade.setFeedback(gradeUpdateDto.feedback());
        }
        gradeRepository.save(grade);
        sendGradeNotificationEmail(grade, false);
    }

    @Override
    public void deleteGrade(Grade grade) {
        grade.getSubmission().setGrade(null);
        gradeRepository.delete(grade);
    }

    private void sendGradeNotificationEmail(Grade grade, boolean created) throws JsonProcessingException {
        String link = "http://localhost:5173/course/" + grade.getSubmission().getAssignment().getModule().getCourse()
                .getId() + "/assignment/" + grade.getSubmission().getAssignment().getId() + "/overview";
        String email = buildEmail(grade.getSubmission().getStudent().getFullName(), grade.getManager().getFullName(),
                grade.getManager().getEmail(), grade.getSubmission().getAssignment().getTitle(), link, created);
        EmailDto emailDto = new EmailDto(grade.getSubmission().getStudent().getEmail(), email);
        this.template.send(gradeTopicName, objectMapper.writeValueAsString(emailDto));
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
