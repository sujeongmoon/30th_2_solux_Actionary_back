package com.req2res.actionarybe.domain.notification.service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateRequestDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateResponseDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationGetResponseDTO;
import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import com.req2res.actionarybe.domain.notification.repository.NotificationRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    // 1. 알림 생성 API
    //직접 HTTP 호출이 아니라 다른 API에서 .create로 호출하는 형식 사용
    // 1-1. 저장 전용 공용 메서드
    @Transactional
    public NotificationCreateResponseDTO create(NotificationCreateRequestDTO request) {

        Member receiver = memberRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Notification notification = Notification.create(
                receiver,
                request.getType(),
                request.getTitle(),
                request.getContent(),
                request.getLink()
        );

        Notification saved = notificationRepository.save(notification);
        return NotificationCreateResponseDTO.from(saved);
    }

    // 1-2. 투두 모두 완료했을 때
    @Transactional
    public void notifyTodoAllDone(Long userId, LocalDate date) {
        NotificationCreateRequestDTO req = NotificationCreateRequestDTO.of(
                userId,
                NotificationType.TODO_ALL_DONE,
                "오늘의 투두를 모두 완료했어요 🎉",
                "오늘(" + date + ")의 투두를 전부 완료했습니다!",
                "/todos?date=" + date
        );
        create(req);
    }

    // 1-3. 포인트 적립 시
    @Transactional
    public void notifyPoint(Long userId, int point, String reason) {
        NotificationCreateRequestDTO req = NotificationCreateRequestDTO.of(
                userId,
                NotificationType.POINT,
                "포인트가 적립되었습니다.",
                reason + "로 " + point + "P가 적립되었어요.",
                "/mypage/points"
        );
        create(req);
    }

    // 1-4. 내 게시물에 댓글 달렸을 때
    @Transactional
    public void notifyComment(Long receiverId, Long postId, String commenterName) {
        NotificationCreateRequestDTO req = NotificationCreateRequestDTO.of(
                receiverId,
                NotificationType.COMMENT,
                "내 게시글에 댓글이 달렸습니다.",
                commenterName + "님이 댓글을 남겼어요.",
                "/posts/" + postId
        );
        create(req);
    }

    // 1-5. 오늘 공부량 리포트 (하루 1번만 생성)
    @Transactional
    public void notifyDailyStudySummary(Long userId, String summaryText) {

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(LocalTime.MAX);

        // 이미 오늘 알림이 있으면 생성하지 않음
        boolean alreadyNotified =
                notificationRepository.existsByReceiverIdAndTypeAndCreatedAtBetween(
                        userId,
                        NotificationType.DAILY_STUDY_SUMMARY,
                        start,
                        end
                );

        if (alreadyNotified) {
            return;
        }

        NotificationCreateRequestDTO req = NotificationCreateRequestDTO.of(
                userId,
                NotificationType.DAILY_STUDY_SUMMARY,
                "오늘 공부량 리포트",
                summaryText,
                "/study/report"
        );

        create(req);
    }


    //-------------------------------------------------
    // 2. 알림 조회 API
    @Transactional(readOnly = true)
    public List<NotificationGetResponseDTO> getMyNotifications(Long memberId, Integer limit) {

        List<Notification> notifications;

        if (limit == null) {
            notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(memberId);
        } else {
            // limit이 0 이하이면 빈 리스트 반환
            if (limit <= 0) {
                return List.of();
            }
            notifications = notificationRepository.findByReceiverIdOrderByCreatedAtDesc(
                    memberId,
                    PageRequest.of(0, limit)
            );
        }

        return notifications.stream()
                .map(NotificationGetResponseDTO::from)
                .toList();
    }
}
