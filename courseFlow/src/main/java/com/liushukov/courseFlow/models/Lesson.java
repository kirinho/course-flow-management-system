package com.liushukov.courseFlow.models;

import jakarta.persistence.*;

import java.util.List;

@Entity
@DiscriminatorValue("LESSON")
public class Lesson extends BaseLessonAssignment {
    @Column(columnDefinition = "TEXT", name = "content", nullable = false)
    private String content;

    public Lesson() {}

    public Lesson(String title, String description, Integer position, Module module, String content) {
        this.setTitle(title);
        this.setDescription(description);
        this.setPosition(position);
        this.setModule(module);
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
