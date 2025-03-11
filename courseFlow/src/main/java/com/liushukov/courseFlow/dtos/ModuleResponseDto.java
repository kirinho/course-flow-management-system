package com.liushukov.courseFlow.dtos;

public record ModuleResponseDto(
        Long id,
        String name,
        String description,
        Integer position
) {
}
