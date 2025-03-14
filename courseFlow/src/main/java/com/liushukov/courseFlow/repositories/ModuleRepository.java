package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    @Query("SELECT m FROM Module m WHERE m.id = :moduleId")
    Optional<Module> findModuleById(@Param("moduleId") long moduleId);

    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId ORDER BY m.position")
    Page<Module> findAllModulesByCourse(@Param("courseId") long courseId, Pageable pageable);
}
