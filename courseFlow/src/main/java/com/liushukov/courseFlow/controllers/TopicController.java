package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.dtos.TopicDto;
import com.liushukov.courseFlow.dtos.UpdateTopicDto;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.Topic;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.TopicService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {

    private final TopicService topicService;

    private final CourseService courseService;

    public TopicController(TopicService topicService, CourseService courseService) {
        this.topicService = topicService;
        this.courseService = courseService;
    }

    @GetMapping("/topic/{topicId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Topic> topic(@PathVariable(value = "topicId") Long topicId) {
        var response = topicService.getTopicById(topicId);
        return (response.isPresent())
                ? ResponseEntity.status(HttpStatus.OK).body(response.get())
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/course/{courseId}/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Topic>> allTopicsByCourse(@PathVariable(name = "courseId") Long courseId) {
        var course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            var response = topicService.getTopicsByCourse(course.get());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<Topic>> allTopics(
            @RequestParam(value = "sortBy", defaultValue = "id") String sortBy,
            @RequestParam(value = "orderBy", defaultValue = "asc") String orderBy,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        SortingOrderEnum order = SortingOrderEnum.valueOf(orderBy.toUpperCase());
        var response = topicService.getAllTopics(order, sortBy, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/course/{courseId}/create-topic")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Topic> createTopic(@PathVariable(name = "courseId") Long courseId, @Valid @RequestBody TopicDto topicDto) {
        var course = courseService.getCourseById(courseId);
        if (course.isPresent()) {
            var response = topicService.saveTopic(course.get(), topicDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PatchMapping("/course/{courseId}/update-topic/{topicId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Topic> updateTopic(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "topicId") Long topicId,
            @Valid @RequestBody UpdateTopicDto updateTopicDto
    ) {
        var topic = topicService.getTopicById(topicId);
        if (topic.isPresent()) {
            if (topic.get().getCourse().getId().equals(courseId)) {
                var response = topicService.updateTopic(topic.get(), updateTopicDto);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/course/{courseId}/delete-topic/{topicId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable(name = "courseId") Long courseId,
            @PathVariable(name = "topicId") Long topicId
    ) {
        var topic = topicService.getTopicById(topicId);
        if (topic.isPresent()) {
            if (topic.get().getCourse().getId().equals(courseId)) {
                topicService.deleteTopic(topic.get());
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
