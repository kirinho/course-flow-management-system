package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.dtos.TopicDto;
import com.liushukov.courseFlow.dtos.UpdateTopicDto;
import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.Topic;
import com.liushukov.courseFlow.repositories.TopicRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TopicService {

    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public Optional<Topic> getTopicById(Long id) {
        return topicRepository.findById(id);
    }

    public List<Topic> getTopicsByCourse(Course course) {
        List<Topic> list = topicRepository.findAll();
        return (!list.isEmpty())
                ? list.stream().filter(topic -> topic.getCourse().getId().equals(course.getId())).toList()
                : Collections.emptyList();
    }

    public List<Topic> getAllTopics(SortingOrderEnum orderBy, String sortBy, int pageNumber, int pageSize) {
        Pageable pageable;
        switch (orderBy) {
            case DESC -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        }
        return topicRepository.findAll(pageable).getContent();
    }

    @Transactional
    public Topic saveTopic(Course course, TopicDto topicDto) {
        var topic = new Topic(
                topicDto.name(),
                topicDto.description(),
                course
        );
        return topicRepository.save(topic);
    }

    @Transactional
    public Topic updateTopic(Topic topic, UpdateTopicDto topicDto) {
        if (topicDto.name() != null) {
            topic.setName(topicDto.name());
        }
        if (topicDto.description() != null) {
            topic.setDescription(topicDto.description());
        }
        return topicRepository.save(topic);
    }

    @Transactional
    public void deleteTopic(Topic topic) {
        topicRepository.delete(topic);
    }
}
