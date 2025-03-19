package com.liushukov.courseFlow.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "created_at")
    private Instant createdAt;
    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "updated_at")
    private Instant updatedAt;
    @Column(name = "score", nullable = false)
    private Integer score;
    @Column(columnDefinition = "TEXT", name = "feedback")
    private String feedback;
    @ManyToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;
    @OneToOne
    @JoinColumn(name = "submission_id", nullable = false)
    private Submission submission;

    public Grade() {}

    public Grade(Integer score, String feedback, User manager, Submission submission) {
        this.score = score;
        this.feedback = feedback;
        this.manager = manager;
        this.submission = submission;
    }

    public Long getId() {
        return id;
    }

    public Integer getScore() {
        return score;
    }

    public String getFeedback() {
        return feedback;
    }

    public User getManager() {
        return manager;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
}
