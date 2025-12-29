package com.req2res.actionarybe.domain.study.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.req2res.actionarybe.domain.study.entity.Category;
import com.req2res.actionarybe.domain.study.entity.Study;

public interface StudyRepository extends JpaRepository<Study, Long> {

	Page<Study> findByIsPublic(boolean isPublic, Pageable pageable);

	Page<Study> findByIsPublicAndCategory(Boolean isPublic, Category category, Pageable pageable);

}
