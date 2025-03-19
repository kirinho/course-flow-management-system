package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface SubmissionService {
    Optional<Submission> getSubmissionById(long id);

    void createSubmission(String textSubmission, User user, Assignment assignment, MultipartFile[] files);

    void updateSubmission(Submission submission, String textSubmission, MultipartFile[] files);

    void deleteSubmission(Submission submission);
}
