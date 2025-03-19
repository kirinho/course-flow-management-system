package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.Submission;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.AssignmentService;
import com.liushukov.courseFlow.services.SubmissionService;
import com.liushukov.courseFlow.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping(path = "/submission")
public class SubmissionController {
    private final UserService userService;
    private final AssignmentService assignmentService;
    private final SubmissionService submissionService;

    public SubmissionController(UserService userService, AssignmentService assignmentService, SubmissionService submissionService) {
        this.userService = userService;
        this.assignmentService = assignmentService;
        this.submissionService = submissionService;
    }

    @PostMapping(path = "/assignment/{assignmentId}/create")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> create(
            Authentication authentication,
            @PathVariable(value = "assignmentId") Long assignmentId,
            @RequestPart(value = "textSubmission", required = false) String textSubmission,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        boolean hasSubmissionContent = textSubmission != null || (attachments != null && attachments.length > 0);
        if (user.isEnabled() && assignment.isPresent() && hasSubmissionContent) {
            submissionService.createSubmission(textSubmission, user, assignment.get(), attachments);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PatchMapping(path = "/{submissionId}/update")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> update(
            @PathVariable(value = "submissionId") Long submissionId,
            @RequestPart(value = "textSubmission", required = false) String textSubmission,
            @RequestPart(value = "attachments", required = false) MultipartFile[] attachments
    ) {
        Optional<Submission> submission = submissionService.getSubmissionById(submissionId);
        boolean hasSubmissionContent = textSubmission != null || (attachments != null && attachments.length > 0);
        if (submission.isPresent() && hasSubmissionContent) {
            submissionService.updateSubmission(submission.get(), textSubmission, attachments);
            return ResponseEntity.status(HttpStatus.OK).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping(path = "/{submissionId}/delete")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> delete(@PathVariable(value = "submissionId") Long submissionId) {
        Optional<Submission> submission = submissionService.getSubmissionById(submissionId);
        if (submission.isPresent()) {
            submissionService.deleteSubmission(submission.get());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
