package com.liushukov.courseFlow.dtos;

public interface StudentTotalGradeProjection {
    Long getStudentId();
    String getFullName();
    Integer getTotalScore();
    Integer getMaxTotalScore();
}
