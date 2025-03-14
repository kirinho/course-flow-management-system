package com.liushukov.courseFlow.models;

import jakarta.persistence.*;

@Entity
@Table(name = "grades")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "score", nullable = false)
    private Integer score;
    @Column(columnDefinition = "TEXT", name = "feedback")
    private String feedback;
    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;
    @OneToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    public Grade() {}

    public Grade(Integer score, String feedback, User teacher, Submission submission) {
        this.score = score;
        this.feedback = feedback;
        this.teacher = teacher;
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

    public User getTeacher() {
        return teacher;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }
}
