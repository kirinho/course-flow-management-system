package com.liushukov.courseFlow.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "modules")
public class Module implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "created_at")
    private Instant createdAt;
    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "updated_at")
    private Instant updatedAt;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(columnDefinition = "TEXT", name = "description", nullable = false)
    private String description;
    @Column(name = "position", nullable = false)
    private Integer position;
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    @JsonIgnore
    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BaseLessonAssignment> lessonAssignments;

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

    public List<BaseLessonAssignment> getLessonAssignments() {
        return lessonAssignments;
    }
}
