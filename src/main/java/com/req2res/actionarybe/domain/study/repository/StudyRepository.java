package com.req2res.actionarybe.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.req2res.actionarybe.domain.study.entity.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {
}
