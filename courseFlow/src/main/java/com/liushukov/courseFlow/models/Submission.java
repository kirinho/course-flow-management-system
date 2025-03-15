package com.liushukov.courseFlow.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "submitted_at")
    private Instant submittedAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User student;
    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
    @Column(columnDefinition = "TEXT", name = "text_submission")
    private String textSubmission;
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    public Submission() {}

    public Submission(Instant submittedAt, User student, Assignment assignment, String textSubmission, List<Attachment> attachments) {
        this.submittedAt = submittedAt;
        this.student = student;
        this.assignment = assignment;
        this.textSubmission = textSubmission;
        this.attachments = attachments;
    }

    public Long getId() {
        return id;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public User getStudent() {
        return student;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public String getTextSubmission() {
        return textSubmission;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setSubmittedAt(Instant submittedAt) {
        this.submittedAt = submittedAt;
    }

    public void setStudent(User student) {
        this.student = student;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public void setTextSubmission(String textSubmission) {
        this.textSubmission = textSubmission;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
}
