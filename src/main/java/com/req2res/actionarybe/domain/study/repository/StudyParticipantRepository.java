package com.req2res.actionarybe.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.req2res.actionarybe.domain.study.entity.StudyParticipant;

public interface StudyParticipantRepository extends JpaRepository<StudyParticipant, Long> {
}
