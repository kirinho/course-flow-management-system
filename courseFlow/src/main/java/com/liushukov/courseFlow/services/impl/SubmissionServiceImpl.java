package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.models.*;
import com.liushukov.courseFlow.repositories.AttachmentRepository;
import com.liushukov.courseFlow.repositories.SubmissionRepository;
import com.liushukov.courseFlow.services.SubmissionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.liushukov.courseFlow.models.AttachmentExtension.*;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    private final SubmissionRepository submissionRepository;
    private final AttachmentRepository attachmentRepository;

    public SubmissionServiceImpl(SubmissionRepository submissionRepository, AttachmentRepository attachmentRepository) {
        this.submissionRepository = submissionRepository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Optional<Submission> getSubmissionById(long id) {
        return submissionRepository.findSubmissionById(id);
    }

    @Transactional
    @Override
    public void createSubmission(String textSubmission, User user, Assignment assignment, MultipartFile[] files) {
        Submission submission = new Submission(user, assignment, textSubmission);
        Submission savedSubmission = submissionRepository.save(submission);
        if (files != null && files.length > 0) {
            saveAttachments(savedSubmission, files);
        }
    }

    @Transactional
    @Override
    public void updateSubmission(Submission submission, String textSubmission, MultipartFile[] files) {
        if (textSubmission != null) {
            submission.setTextSubmission(textSubmission);
        }
        Submission updatedSubmission = submissionRepository.save(submission);
        if (files != null && files.length > 0) {
            saveAttachments(updatedSubmission, files);
        }
    }

    @Transactional
    @Override
    public void deleteSubmission(Submission submission) {
        submission.setTextSubmission(null);
        attachmentRepository.deleteBySubmission(submission);
        submissionRepository.save(submission);
    }

    private void saveAttachments(Submission submission, MultipartFile[] files) {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                attachments.add(new Attachment(
                        file.getOriginalFilename(),
                        getFileExtension(file.getOriginalFilename()),
                        file.getBytes(),
                        submission
                ));
            } catch (IOException e) {
                throw new RuntimeException("Error processing file: " + file.getOriginalFilename(), e);
            }
        }
        attachmentRepository.saveAll(attachments);
    }

    private AttachmentExtension getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("Invalid file name: " + fileName);
        }
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return switch (extension) {
            case "docx" -> DOCX;
            case "doc" -> DOC;
            case "pdf" -> PDF;
            case "xlsx" -> XSLX;
            case "xls" -> XSL;
            default -> UNSUPPORTED;
        };
    }
}
