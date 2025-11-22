package com.req2res.actionarybe.domain.studyTime.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.req2res.actionarybe.domain.studyTime.entity.StudyTime;

public interface StudyTimeRepository extends JpaRepository<StudyTime, Long> {
}
