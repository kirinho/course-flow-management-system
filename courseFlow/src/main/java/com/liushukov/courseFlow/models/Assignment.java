package com.liushukov.courseFlow.models;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Date;

@Entity
@DiscriminatorValue("ASSIGNMENT")
public class Assignment extends BaseLessonAssignment {
    @Column(name = "due_date", columnDefinition = "DATE")
    @Temporal(TemporalType.DATE)
    private Date dueDate;
    @Column(name = "max_score")
    private Integer maxScore;

    public Assignment() {}

    public Assignment(String title, String description, Integer position, Date dueDate, Integer maxScore, Module module) {
        this.setTitle(title);
        this.setDescription(description);
        this.setPosition(position);
        this.dueDate = dueDate;
        this.maxScore = maxScore;
        this.setModule(module);
    }

    public Assignment(Date dueDate, Integer maxScore) {
        this.dueDate = dueDate;
        this.maxScore = maxScore;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Integer getMaxScore() {
        return maxScore;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setMaxScore(Integer maxScore) {
        this.maxScore = maxScore;
    }
}
