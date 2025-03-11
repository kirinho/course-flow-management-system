package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.models.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    @Query(value = "SELECT * FROM modules WHERE id = ?1", nativeQuery = true)
    Optional<Module> findModuleById(long moduleId);

    @Query(value = "SELECT * FROM modules WHERE course_id = ?1 ORDER BY position", nativeQuery = true)
    Page<Module> findAllModulesByCourse(long courseId, Pageable pageable);
}
