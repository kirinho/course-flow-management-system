package com.liushukov.courseFlow.models;

import jakarta.persistence.*;

@Entity
@Table(name = "attachments")
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "file_name", nullable = false)
    private String fileName;
    @Enumerated(EnumType.STRING)
    @Column(name = "file_type", nullable = false)
    private AttachmentExtension fileType;
    @Column(columnDefinition = "BYTEA", name = "file_data")
    private byte[] fileData;
    @ManyToOne
    @JoinColumn(name = "lesson_assignment_id")
    private BaseLessonAssignment lessonAssignment;
    @ManyToOne
    @JoinColumn(name = "submission_id")
    private Submission submission;

    public Attachment(String fileName, AttachmentExtension fileType, byte[] fileData, BaseLessonAssignment lessonAssignment) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
        this.lessonAssignment = lessonAssignment;
    }

    public Attachment() {}

    public String getFileName() {
        return fileName;
    }

    public Attachment setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public AttachmentExtension getFileType() {
        return fileType;
    }

    public Attachment setFileType(AttachmentExtension fileType) {
        this.fileType = fileType;
        return this;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public Attachment setFileData(byte[] fileData) {
        this.fileData = fileData;
        return this;
    }

    public BaseLessonAssignment getLessonAssignment() {
        return lessonAssignment;
    }

    public Attachment setLessonAssignment(BaseLessonAssignment lessonAssignment) {
        this.lessonAssignment = lessonAssignment;
        return this;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Attachment setSubmission(Submission submission) {
        this.submission = submission;
        return this;
    }

    public Long getId() {
        return id;
    }
}
