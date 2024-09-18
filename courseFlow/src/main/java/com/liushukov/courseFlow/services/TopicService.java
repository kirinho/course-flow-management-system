package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.models.SortingOrderEnum;
import com.liushukov.courseFlow.models.Topic;
import com.liushukov.courseFlow.repositories.TopicRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
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

    public List<Topic> getAllTopics(SortingOrderEnum orderBy, String sortBy, int pageNumber, int pageSize) {
        Pageable pageable;
        switch (orderBy) {
            case DESC -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy).descending());
            default -> pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        }
        return topicRepository.findAll(pageable).getContent();
    }
}
