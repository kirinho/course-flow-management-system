package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    @Query("SELECT g FROM Grade g WHERE g.id = :gradeId")
    Optional<Grade> findGradeById(@Param("gradeId") Long gradeId);
}
