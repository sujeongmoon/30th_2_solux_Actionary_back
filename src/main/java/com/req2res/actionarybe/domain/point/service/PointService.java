package com.req2res.actionarybe.domain.point.service;
//포인트 적립 Service

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.notification.service.NotificationService;
import com.req2res.actionarybe.domain.point.dto.*;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeManualRepository;
import com.req2res.actionarybe.domain.studyTime.repository.StudyTimeRepository;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import com.req2res.actionarybe.domain.todo.repository.TodoRepository;
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
    private final TodoRepository todoRepository;
    private final StudyTimeRepository studyTimeRepository;
    private final StudyTimeManualRepository studyTimeManualRepository;

    // 1. 공부시간 포인트 적립 API
    @Transactional
    public StudyTimePointResponseDTO earnStudyTimePoint(Long loginMemberId) {

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

        // 3) 오늘 누적 공부시간(초) 계산: study_time + study_time_manual
        long studySecondsFromStudy = studyTimeRepository.sumTodaySeconds(member.getId(), start, end);
        long studySecondsFromManual = studyTimeManualRepository
                .sumDurationSecondByUserIdAndManualDate(member.getId(), today);

        long todayStudySeconds = studySecondsFromStudy + studySecondsFromManual;

        // 4) 오늘 누적이 0이면 적립 의미가 없으니 막기 (400)
        if (todayStudySeconds <= 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "오늘 기록된 공부시간이 없습니다.");
        }

        // 5) 적립 포인트 계산: (오늘 누적 초 -> 시간 환산) * 10, 반올림
        int earnedPoint = (int) Math.round((todayStudySeconds / 3600.0) * 10);

        LocalDateTime now = LocalDateTime.now();

        // 6) user_point 조회/생성 (member_id 기준)
        UserPoint userPoint = userPointRepository.findByMember_Id(member.getId())
                .orElseGet(() -> userPointRepository.save(
                        UserPoint.builder()
                                .member(member)
                                .totalPoint(0)
                                .lastEarnedAt(null)
                                .build()
                ));

        // 7) 여기부터는 영속 상태 → save 호출 필요 없음 (dirty checking)
        userPoint.addPoint(earnedPoint, now);

        // 8) point_history 기록 저장
        PointHistory history = PointHistory.builder()
                .member(member)
                .earnedPoint(earnedPoint)
                .source(PointSource.STUDY_TIME)
                .totalPoint(userPoint.getTotalPoint())
                .build();

        pointHistoryRepository.save(history);

        // 9) 알림 생성
        notificationService.notifyPoint(member.getId(), earnedPoint, PointSource.STUDY_TIME);

        return new StudyTimePointResponseDTO(
                member.getId(),
                earnedPoint,
                PointSource.STUDY_TIME,
                todayStudySeconds,
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

    @Transactional
    public TodoCompletionPointResponseDTO earnTodoCompletionPoint(
            Long loginMemberId,
            TodoCompletionPointRequestDTO request
    ) {
        Member member = memberRepository.findById(loginMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 1) to-do 존재 확인 (404)
        Todo todo = todoRepository.findById(request.getTodoId())
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        // 2) to-do 소유자 확인 (403/400 중 선택) - 보통 403
        if (!todo.getUserId().equals(member.getId())) {
            throw new CustomException(ErrorCode.POINT_USER_MISMATCH);
        }

        // 3) 이미 이 to-do로 포인트 지급했는지 (409)
        boolean alreadyRewardedForTodo = pointHistoryRepository.existsByMember_IdAndSourceAndTodoId(
                member.getId(),
                PointSource.TODO_COMPLETION,
                request.getTodoId()
        );

        if (alreadyRewardedForTodo) {
            throw new CustomException(ErrorCode.TODO_POINT_ALREADY_EARNED);

        }

        // 4) 오늘 투두 포인트 지급 개수 확인 (일 최대 5P)
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        int limit = 5;
        int todayCount = (int) pointHistoryRepository.countByMember_IdAndSourceAndCreatedAtBetween(
                member.getId(),
                PointSource.TODO_COMPLETION,
                start,
                end
        );

        // 5) 한도 초과면 0P로 정상 응답 (history 저장 X, 알림 X)
        UserPoint userPoint = userPointRepository.findByMember_Id(member.getId())
                .orElseGet(() -> userPointRepository.save(
                        UserPoint.builder()
                                .member(member)
                                .totalPoint(0)
                                .lastEarnedAt(null)
                                .build()
                ));

        if (todayCount >= limit) {
            return new TodoCompletionPointResponseDTO(
                    member.getId(),
                    request.getTodoId(),
                    0,
                    PointSource.TODO_COMPLETION,
                    limit,
                    limit,
                    userPoint.getTotalPoint()
            );
        }

        // 6) 적립 (1P)
        int earnedPoint = 1;
        LocalDateTime now = LocalDateTime.now();
        userPoint.addPoint(earnedPoint, now);

        // 7) point_history 저장 (todoId 포함)
        PointHistory history = PointHistory.builder()
                .member(member)
                .todoId(request.getTodoId())
                .earnedPoint(earnedPoint)
                .source(PointSource.TODO_COMPLETION)
                .totalPoint(userPoint.getTotalPoint())
                .build();
        pointHistoryRepository.save(history);

        // 8) 알림 생성
        notificationService.notifyPoint(member.getId(), earnedPoint, PointSource.TODO_COMPLETION);

        return new TodoCompletionPointResponseDTO(
                member.getId(),
                request.getTodoId(),
                earnedPoint,
                PointSource.TODO_COMPLETION,
                todayCount + 1,
                limit,
                userPoint.getTotalPoint()
        );
    }


}