package com.req2res.actionarybe.domain.point.repository;

import com.req2res.actionarybe.domain.point.entity.PointHistory;
import com.req2res.actionarybe.domain.point.entity.PointSource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    // "오늘 STUDY_TIME 적립" 중복 여부 체크
    boolean existsByMember_IdAndSourceAndCreatedAtBetween(
            Long memberId,
            PointSource source,
            LocalDateTime start,
            LocalDateTime end
    );

    // "스터디 참여 포인트 적립" 중복 여부 체크
    boolean existsByMember_IdAndSourceAndStudyRoomId(
            Long memberId,
            PointSource source,
            Long studyRoomId
    );

    // "투두 완료 포인트 적립" 중복 여부 체크
    boolean existsByMember_IdAndSourceAndTodoId(Long memberId, PointSource source, Long todoId);

    // 오늘 투두 포인트 지급 횟수 카운트(일 최대 5P 제한)
    long countByMember_IdAndSourceAndCreatedAtBetween(Long memberId, PointSource source,
                                                      LocalDateTime start, LocalDateTime end);
}