package com.req2res.actionarybe.domain.point.service;
// 포인트 조회 Service

import com.req2res.actionarybe.domain.member.entity.Badge;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.point.dto.*;
import com.req2res.actionarybe.domain.point.entity.PointSource;
import com.req2res.actionarybe.domain.point.repository.PointHistoryRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserPointService {

    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;

    // 1. 공개용 포인트 조회 API
    public PublicUserPointResponseDTO getPublicUserPoints(Long userId) {

        // 1) 사용자 존재 확인 (404)
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 2) source별 포인트 합계
        int study = pointHistoryRepository.sumEarnedPointByMemberAndSource(member.getId(), PointSource.STUDY_TIME);
        int studyParticipation = pointHistoryRepository.sumEarnedPointByMemberAndSource(member.getId(), PointSource.STUDY_PARTICIPATION);
        int todo = pointHistoryRepository.sumEarnedPointByMemberAndSource(member.getId(), PointSource.TODO_COMPLETION);

        int total = study + studyParticipation + todo;

        PublicPointsDTO points = new PublicPointsDTO(study, studyParticipation, todo, total);

        // 3) badges 구성 (Member가 Badge 연관관계를 들고 있으므로 그대로 사용)
        List<PublicBadgeDTO> badges = Collections.emptyList();

        Badge badge = member.getBadge();
        if (badge != null) {
            badges = List.of(new PublicBadgeDTO(badge.getId(), badge.getName()));
        }

        return new PublicUserPointResponseDTO(
                member.getId(),
                member.getNickname(),
                points,
                badges
        );
    }
}
