package com.req2res.actionarybe.domain.point.repository;

import com.req2res.actionarybe.domain.point.entity.PointHistory;
import com.req2res.actionarybe.domain.point.entity.PointSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // "오늘 STUDY_TIME 적립" 중복 여부 체크 (409 Conflict)
    boolean existsByMember_IdAndSourceAndCreatedAtBetween(
            Long memberId,
            PointSource source,
            LocalDateTime start,
            LocalDateTime end
    );
}