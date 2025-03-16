package com.liushukov.courseFlow.dtos;

public record AttachmentDownloadResponseDto(
        String fileName,
        byte[] fileData
) {
}
