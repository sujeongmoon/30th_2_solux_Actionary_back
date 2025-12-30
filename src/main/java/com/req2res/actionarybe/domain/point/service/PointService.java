package com.req2res.actionarybe.domain.point.service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.notification.service.NotificationService;
import com.req2res.actionarybe.domain.point.dto.StudyParticipationPointRequestDTO;
import com.req2res.actionarybe.domain.point.dto.StudyParticipationPointResponseDTO;
import com.req2res.actionarybe.domain.point.dto.StudyTimePointRequestDTO;
import com.req2res.actionarybe.domain.point.dto.StudyTimePointResponseDTO;
import com.req2res.actionarybe.domain.point.entity.PointHistory;
import com.req2res.actionarybe.domain.point.entity.PointSource;
import com.req2res.actionarybe.domain.point.entity.UserPoint;
import com.req2res.actionarybe.domain.point.repository.PointHistoryRepository;
import com.req2res.actionarybe.domain.point.repository.UserPointRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PointService {

    private final MemberRepository memberRepository;
    private final UserPointRepository userPointRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final NotificationService notificationService;

    // 1. 공부시간 포인트 적립 API
    @Transactional
    public StudyTimePointResponseDTO earnStudyTimePoint(Long loginMemberId,
                                                        StudyTimePointRequestDTO request) {

        // 1) 사용자 존재 확인 (404)
        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 2) 중복 적립 방지 (오늘 STUDY_TIME 적립했으면 409)
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        boolean alreadyEarned = pointHistoryRepository.existsByMember_IdAndSourceAndCreatedAtBetween(
                member.getId(),
                PointSource.STUDY_TIME,
                start,
                end
        );

        if (alreadyEarned) {
            throw new CustomException(ErrorCode.STUDY_TIME_POINT_ALREADY_EARNED_TODAY);
        }

        // 3) 적립 포인트 계산 (studyHours * 10, 반올림)
        int earnedPoint = (int) Math.round(request.getStudyHours() * 10);
        LocalDateTime now = LocalDateTime.now();

        // 4) user_point 조회/생성 (member_id 기준)
        UserPoint userPoint = userPointRepository.findByMember_Id(member.getId())
                .orElseGet(() -> userPointRepository.save(
                        UserPoint.builder()
                                .member(member)
                                .totalPoint(0)
                                .lastEarnedAt(null)
                                .build()
                ));

        // 5) 여기부터는 영속 상태 → save 호출 필요 없음 (dirty checking)
        userPoint.addPoint(earnedPoint, now);

        // 6) point_history 기록 저장 (createdAt은 Timestamped가 자동 처리)
        PointHistory history = PointHistory.builder()
                .member(member)
                .earnedPoint(earnedPoint)
                .source(PointSource.STUDY_TIME)
                .totalPoint(userPoint.getTotalPoint())
                .build();

        pointHistoryRepository.save(history);

        // 7) 알림 생성
        notificationService.notifyPoint(member.getId(), earnedPoint, PointSource.STUDY_TIME);

        return new StudyTimePointResponseDTO(
                member.getId(),
                earnedPoint,
                PointSource.STUDY_TIME,
                request.getStudyHours(),
                userPoint.getTotalPoint()
        );
    }

    // 2. 스터디 참여 포인트 적립 API
    @Transactional
    public StudyParticipationPointResponseDTO earnStudyParticipationPoint(
            Long loginMemberId,
            StudyParticipationPointRequestDTO request
    ) {
        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 30분 이상만 인정 (400)
        if (request.getParticipatedMinutes() == null || request.getParticipatedMinutes() < 30) {
            throw new CustomException(ErrorCode.STUDY_PARTICIPATION_TIME_NOT_ENOUGH);
        }

        // 중복 지급 방지 (409) : 같은 studyRoomId에 대해 이미 지급했는지
        boolean alreadyRewarded = pointHistoryRepository.existsByMember_IdAndSourceAndStudyRoomId(
                member.getId(),
                PointSource.STUDY_PARTICIPATION,
                request.getStudyRoomId()
        );

        if (alreadyRewarded) {
            throw new CustomException(ErrorCode.STUDY_PARTICIPATION_POINT_ALREADY_EARNED_TODAY);
        }

        // 고정 10P
        int earnedPoint = 10;
        LocalDateTime now = LocalDateTime.now();

        // user_point 조회/생성 (member 기준)
        UserPoint userPoint = userPointRepository.findByMember_Id(member.getId())
                .orElseGet(() -> userPointRepository.save(
                        UserPoint.builder()
                                .member(member)
                                .totalPoint(0)
                                .lastEarnedAt(null)
                                .build()
                ));

        // 포인트 반영
        userPoint.addPoint(earnedPoint, now);

        // point_history 저장 (studyRoomId 포함!)
        PointHistory history = PointHistory.builder()
                .member(member)
                .studyRoomId(request.getStudyRoomId())
                .earnedPoint(earnedPoint)
                .source(PointSource.STUDY_PARTICIPATION)
                .totalPoint(userPoint.getTotalPoint())
                .build();

        pointHistoryRepository.save(history);

        // 알림 생성
        notificationService.notifyPoint(member.getId(), earnedPoint, PointSource.STUDY_PARTICIPATION);

        return new StudyParticipationPointResponseDTO(
                member.getId(),
                request.getStudyRoomId(),
                earnedPoint,
                PointSource.STUDY_PARTICIPATION,
                userPoint.getTotalPoint()
        );
    }

}