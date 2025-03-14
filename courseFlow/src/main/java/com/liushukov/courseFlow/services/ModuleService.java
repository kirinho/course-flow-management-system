package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.ModuleCreateDto;
import com.liushukov.courseFlow.dtos.ModuleResponseDto;
import com.liushukov.courseFlow.dtos.ModuleUpdateDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Module;

import java.util.List;
import java.util.Optional;

public interface ModuleService {
    Optional<Module> getModuleById(long moduleId);

    ModuleResponseDto toDto(Module module);

    List<ModuleResponseDto> getAllModulesByCourse(Course course, int pageNumber, int pageSize);

    void createModule(ModuleCreateDto moduleCreateDto, Course course);

    void updateModule(Module module, ModuleUpdateDto moduleUpdateDto);

    void deleteModule(Module module);
}
