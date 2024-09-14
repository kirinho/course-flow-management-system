package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Course;
import com.liushukov.courseFlow.models.CourseTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByName(String name);
}
