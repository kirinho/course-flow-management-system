package com.liushukov.courseFlow.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@Table(name = "lessons_assignments")
public abstract class BaseLessonAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "created_at", updatable = false)
    private Instant createdAt;
    @UpdateTimestamp
    @Column(columnDefinition = "TIMESTAMP", name = "updated_at")
    private Instant updatedAt;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "type", insertable = false, updatable = false)
    private String type;
    @Column(name = "position", nullable = false)
    private Integer position;
    @ManyToOne
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    @JsonIgnore
    @OneToMany(mappedBy = "lessonAssignment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attachment> attachments;

    public BaseLessonAssignment() {}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public Integer getPosition() {
        return position;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public Module getModule() {
        return module;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
