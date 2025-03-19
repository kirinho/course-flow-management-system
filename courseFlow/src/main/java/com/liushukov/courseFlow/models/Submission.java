package com.liushukov.courseFlow.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "submitted_at", columnDefinition = "DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date submittedAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User student;
    @ManyToOne
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;
    @Column(columnDefinition = "TEXT", name = "text_submission")
    private String textSubmission;
    @JsonIgnore
    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;
    @JsonIgnore
    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Grade grade;

    public Submission() {}

    public Submission(User student, Assignment assignment, String textSubmission) {
        this.student = student;
        this.assignment = assignment;
        this.textSubmission = textSubmission;
    }

    public Long getId() {
        return id;
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

    public void setStudent(User student) {
        this.student = student;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public void setTextSubmission(String textSubmission) {
        this.textSubmission = textSubmission;
    }

    public Date getSubmittedAt() {
        return submittedAt;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    @PrePersist
    @PreUpdate
    public void setSubmittedAt() {
        if (submittedAt == null) {
            submittedAt = java.sql.Date.valueOf(java.time.LocalDate.now());
        }
    }
}
