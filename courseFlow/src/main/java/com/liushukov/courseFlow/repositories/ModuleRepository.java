package com.liushukov.courseFlow.repositories;

import com.liushukov.courseFlow.dtos.ModuleProjection;
import com.liushukov.courseFlow.models.Module;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {
    @Query("SELECT m FROM Module m WHERE m.id = :moduleId")
    Optional<Module> findModuleById(@Param("moduleId") long moduleId);

    @Query("SELECT m FROM Module m WHERE m.course.id = :courseId ORDER BY m.position")
    Page<Module> findAllModulesByCourse(@Param("courseId") long courseId, Pageable pageable);

    @Query("""
            SELECT m.id FROM Module m
            WHERE m.course.id = :courseId
            ORDER BY m.position
            """)
    Page<Long> findModulesIdsByCourse(@Param("courseId") long courseId, Pageable pageable);

    @Query("""
            SELECT m FROM Module m
            LEFT JOIN FETCH m.lessonAssignments la
            WHERE m.id IN :ids
            ORDER BY m.position, la.position
            """)
    List<Module> findModulesWithLessonsAndAssignments(@Param("ids") List<Long> ids);

    @Query("""
            SELECT m.name as moduleName,
                   a.title as assignmentTitle,
                   u.id as studentId,
                   u.fullName as studentFullName,
                   g.score as score,
                   a.maxScore as maxScore
            FROM Module m
            LEFT JOIN m.lessonAssignments a
            LEFT JOIN a.submissions s
            LEFT JOIN s.grade g
            LEFT JOIN s.student u
            WHERE m.course.id = :courseId
            """)
    List<ModuleProjection> findAllModulesWithAssignmentsByCourseId(@Param("courseId") Long courseId);
}
