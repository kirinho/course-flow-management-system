package com.liushukov.courseFlow.dtos;

public interface ModuleProjection {
    String getModuleName();

    String getAssignmentTitle();

    Long getStudentId();

    String getStudentFullName();

    Integer getScore();

    Integer getMaxScore();
}
