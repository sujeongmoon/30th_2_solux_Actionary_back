package com.req2res.actionarybe.domain.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.study.entity.Study;
import com.req2res.actionarybe.domain.study.entity.StudyLike;

public interface StudyLikeRepository extends JpaRepository<StudyLike, Long> {
	boolean existsByStudyAndMember(Study study, Member member);
}
