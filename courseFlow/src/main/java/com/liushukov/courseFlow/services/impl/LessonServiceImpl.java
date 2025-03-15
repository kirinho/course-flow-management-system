package com.liushukov.courseFlow.services.impl;

import com.liushukov.courseFlow.dtos.LessonCreateDto;
import com.liushukov.courseFlow.dtos.LessonUpdateDto;
import com.liushukov.courseFlow.models.Attachment;
import com.liushukov.courseFlow.models.AttachmentExtension;
import com.liushukov.courseFlow.models.Lesson;
import com.liushukov.courseFlow.models.Module;
import com.liushukov.courseFlow.repositories.AttachmentRepository;
import com.liushukov.courseFlow.repositories.BaseLessonAssignmentRepository;
import com.liushukov.courseFlow.services.LessonService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.liushukov.courseFlow.models.AttachmentExtension.*;

@Service
public class LessonServiceImpl implements LessonService {
    private final BaseLessonAssignmentRepository repository;
    private final AttachmentRepository attachmentRepository;

    public LessonServiceImpl(BaseLessonAssignmentRepository repository, AttachmentRepository attachmentRepository) {
        this.repository = repository;
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public Optional<Lesson> getLessonById(long id) {
        return repository.findLessonById(id);
    }

    @Transactional
    @Override
    public void createLesson(LessonCreateDto lessonCreateDto, Module module, MultipartFile[] files) {
        Lesson lesson = new Lesson(
                lessonCreateDto.title(),
                lessonCreateDto.description(),
                lessonCreateDto.position(),
                module,
                lessonCreateDto.content()
        );
        Lesson savedLesson = repository.save(lesson);
        if (files != null && files.length > 0) {
            saveAttachments(savedLesson, files);
        }
    }

    @Transactional
    @Override
    public void updateLesson(Lesson lesson, LessonUpdateDto lessonUpdateDto, MultipartFile[] files) {
        if (lessonUpdateDto.title() != null) {
            lesson.setTitle(lessonUpdateDto.title());
        }
        if (lessonUpdateDto.description() != null) {
            lesson.setDescription(lessonUpdateDto.description());
        }
        if (lessonUpdateDto.content() != null) {
            lesson.setContent(lessonUpdateDto.content());
        }
        lesson.setPosition(lessonUpdateDto.position());
        Lesson updatedLesson = repository.save(lesson);
        if (files != null && files.length > 0) {
            attachmentRepository.deleteByLessonAssignment(updatedLesson);
            saveAttachments(updatedLesson, files);
        }
    }

    @Override
    public void deleteLesson(Lesson lesson) {
        repository.delete(lesson);
    }

    private void saveAttachments(Lesson lesson, MultipartFile[] files) {
        List<Attachment> attachments = new ArrayList<>();
        for (MultipartFile file : files) {
            try {
                attachments.add(new Attachment(
                        file.getOriginalFilename(),
                        getFileExtension(file.getOriginalFilename()),
                        file.getBytes(),
                        lesson
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
