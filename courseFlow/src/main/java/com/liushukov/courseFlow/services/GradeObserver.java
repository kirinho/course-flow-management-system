package com.liushukov.courseFlow.services;

import com.liushukov.courseFlow.models.Grade;

public interface GradeObserver {
    void onGradeChange(Grade grade, boolean created);
}
