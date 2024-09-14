package com.liushukov.courseFlow.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.io.Serializable;
import java.time.Instant;

@Table(name = "courses")
@Entity
public class Course implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description", length = 3000, nullable = false)
    private String description;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private CourseTypeEnum typeEnum;

    @CreationTimestamp
    @Column(name = "createdAt")
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updateAt")
    private Instant updateAt;

    public Course() {}

    public Course(String name, String description, CourseTypeEnum typeEnum) {
        this.name = name;
        this.description = description;
        this.typeEnum = typeEnum;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CourseTypeEnum getTypeEnum() {
        return typeEnum;
    }

    public void setTypeEnum(CourseTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }

}
