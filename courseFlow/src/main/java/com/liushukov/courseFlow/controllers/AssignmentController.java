package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.AssignmentResponseDto;
import com.liushukov.courseFlow.dtos.AssignmentUserResponseDto;
import com.liushukov.courseFlow.models.Assignment;
import com.liushukov.courseFlow.models.User;
import com.liushukov.courseFlow.services.AssignmentService;
import com.liushukov.courseFlow.services.EnrollmentService;
import com.liushukov.courseFlow.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/assignment")
public class AssignmentController {
    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final AssignmentService assignmentService;

    public AssignmentController(UserService userService, EnrollmentService enrollmentService, AssignmentService assignmentService) {
        this.userService = userService;
        this.enrollmentService = enrollmentService;
        this.assignmentService = assignmentService;
    }

    @GetMapping(path = "/{assignmentId}/overview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AssignmentResponseDto> assignmentOverview(
            Authentication authentication,
            @PathVariable(value = "assignmentId") Long assignmentId,
            @RequestParam(value = "courseId") Long courseId,
            @RequestParam(value = "userId", required = false) Long userId
    ) {
        User user = userService.getUserFromAuthentication(authentication);
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        if (user.isEnabled() && assignment.isPresent() && assignment.get().getModule().getCourse().getId().equals(courseId)) {
            if (userId != null && assignment.get().getModule().getCourse().getUser().equals(user)) {
                return ResponseEntity.status(HttpStatus.OK).body(assignmentService.getAssignmentOverview(assignmentId,
                        userId));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(assignmentService.getAssignmentOverview(assignmentId,
                        user.getId()));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping(path = "/{assignmentId}/all-students")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AssignmentUserResponseDto>> users(
            @PathVariable(value = "assignmentId") Long assignmentId
    ) {
        Optional<Assignment> assignment = assignmentService.getAssignmentById(assignmentId);
        return assignment
                .map(value -> ResponseEntity.status(HttpStatus.OK)
                        .body(enrollmentService.getUsersByCourse(value.getModule().getCourse().getId())))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
