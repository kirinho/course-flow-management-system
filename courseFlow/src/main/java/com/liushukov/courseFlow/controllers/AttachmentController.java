package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.AttachmentDownloadResponseDto;
import com.liushukov.courseFlow.models.Attachment;
import com.liushukov.courseFlow.models.AttachmentExtension;
import com.liushukov.courseFlow.repositories.AttachmentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@RestController
@RequestMapping(path = "/attachment")
public class AttachmentController {
    private final AttachmentRepository attachmentRepository;

    public AttachmentController(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @GetMapping("/{fileId}")
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
}
