package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.AttachmentDownloadResponseDto;
import com.liushukov.courseFlow.models.Attachment;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.repositories.AttachmentRepository;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.StudentReportService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping(path = "/attachment")
public class AttachmentController {
    private final AttachmentRepository attachmentRepository;
    private final StudentReportService studentReportService;
    private final CourseService courseService;

    public AttachmentController(AttachmentRepository attachmentRepository, StudentReportService studentReportService,
                                CourseService courseService) {
        this.attachmentRepository = attachmentRepository;
        this.studentReportService = studentReportService;
        this.courseService = courseService;
    }

    @GetMapping("/{fileId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<AttachmentDownloadResponseDto> getFile(@PathVariable Long fileId) {
        Optional<Attachment> attachmentOpt = attachmentRepository.findById(fileId);
        if (attachmentOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        AttachmentDownloadResponseDto responseDto = new AttachmentDownloadResponseDto(
                attachmentOpt.get().getFileName(),
                attachmentOpt.get().getFileData()
        );
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @GetMapping("/generate/{courseId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<AttachmentDownloadResponseDto> modules(
            @PathVariable(value = "courseId") Long courseId
    ) throws IOException {
        Optional<Course> course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            byte[] attachment = studentReportService.generateExcelReport(courseId);
            AttachmentDownloadResponseDto attachmentDownloadResponseDto = new AttachmentDownloadResponseDto(
                    "Report_" + course.get().getName() + "_" + LocalDate.now() + ".xlsx",
                    attachment
            );
            return ResponseEntity.status(HttpStatus.OK).body(attachmentDownloadResponseDto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
