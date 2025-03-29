package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.dtos.*;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.repositories.ModuleRepository;
import com.liushukov.courseFlow.services.ModuleService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleServiceImpl implements ModuleService {
    private final ModuleRepository moduleRepository;

    public ModuleServiceImpl(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @Cacheable(value = "modules", key = "#moduleId", unless = "#result == null")
    @Override
    public Optional<Module> getModuleById(long moduleId) {
        return moduleRepository.findModuleById(moduleId);
    }

    @Override
    public ModuleResponseDto toDto(Module module) {
        return new ModuleResponseDto(
                module.getId(),
                module.getName(),
                module.getDescription(),
                module.getPosition()
        );
    }

    @Override
    public List<ModuleResponseDto> getAllModulesByCourse(Course course, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        return moduleRepository
                .findAllModulesByCourse(course.getId(), pageable).getContent().stream().map(this::toDto).toList();
    }

    @Override
    public void createModule(ModuleCreateDto moduleCreateDto, Course course) {
        Module module = new Module(
                moduleCreateDto.name(),
                moduleCreateDto.description(),
                moduleCreateDto.position(), course
        );
        moduleRepository.save(module);
    }

    @CacheEvict(value = "modules", key = "#module.id", condition = "#module != null")
    @Override
    public void updateModule(Module module, ModuleUpdateDto moduleUpdateDto) {
        if (moduleUpdateDto.name() != null) {
            module.setName(moduleUpdateDto.name());
        }
        if (moduleUpdateDto.description() != null) {
            module.setDescription(moduleUpdateDto.description());
        }
        module.setPosition(moduleUpdateDto.position());
        moduleRepository.save(module);
    }

    @CacheEvict(value = "modules", key = "#module.id", condition = "#module != null")
    @Override
    public void deleteModule(Module module) {
        moduleRepository.delete(module);
    }
}
