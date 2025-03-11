package com.liushukov.courseFlow.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "modules")
public class Module {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "created_at")
    private Instant createdAt;
    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "updated_at")
    private Instant updatedAt;
    @Column(name = "name")
    private String name;
    @Column(columnDefinition = "TEXT", name = "description")
    private String description;
    @Column(name = "position")
    private Integer position;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    public Module(String name, String description, Integer position, Course course) {
        this.name = name;
        this.description = description;
        this.position = position;
        this.course = course;
    }

    public Module() {}

    public Long getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPosition() {
        return position;
    }

    public Course getCourse() {
        return course;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
