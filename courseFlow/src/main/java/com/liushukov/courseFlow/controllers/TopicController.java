package com.liushukov.courseFlow.controllers;

import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.Topic;
import com.liushukov.courseFlow.services.CourseService;
import com.liushukov.courseFlow.services.TopicService;
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

    @GetMapping("/all")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Topic>> allTopics(
            @RequestParam(value = "sortBy", defaultValue = "asc") String sortBy,
            @RequestParam(value = "orderBy", defaultValue = "id") String orderBy,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize
    ) {
        SortingOrderEnum order = SortingOrderEnum.valueOf(sortBy.toUpperCase());
        var response = topicService.getAllTopics(order, sortBy, pageNumber, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
