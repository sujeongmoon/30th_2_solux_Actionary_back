package com.req2res.actionarybe.domain.notification.service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateRequestDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateResponseDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationGetResponseDTO;
import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.entity.NotificationType;
import com.req2res.actionarybe.domain.notification.repository.NotificationRepository;
import com.req2res.actionarybe.domain.point.entity.PointSource;
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

        // 정책: COMMENT만 link 허용, 나머지는 link = null
        String link = (request.getType() == NotificationType.COMMENT)
                ? request.getLink()
                : null;

        Notification notification = Notification.create(
                receiver,
                request.getType(),
                request.getTitle(),
                request.getContent(),
                link
        );

        Notification saved = notificationRepository.save(notification);
        return NotificationCreateResponseDTO.from(saved);
    }

    // 1-2. 포인트 적립 시
    @Transactional
    public void notifyPoint(Long userId, int point, PointSource source) {

        String reason = switch (source) {
            case STUDY_TIME -> "공부시간";
            case STUDY_PARTICIPATION -> "스터디 참여";
            case TODO_COMPLETION -> "투두 완료";
        };

        NotificationCreateRequestDTO req = NotificationCreateRequestDTO.of(
                userId,
                NotificationType.POINT,
                "포인트가 적립되었습니다.",
                reason + "로 " + point + "P가 적립되었어요.",
                null
        );
        create(req);
    }

    // 1-3. 내 게시물에 댓글 달렸을 때
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

    // 1-4. 오늘 공부량 리포트 (하루 1번만 생성)
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
                null
        );

        create(req);
    }

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

    // 3.알림 읽음 처리 API
    @Transactional
    public NotificationGetResponseDTO markAsRead(Long memberId, Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        // 내 알림인지 검증
        if (!notification.getReceiver().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.NOTIFICATION_FORBIDDEN);
        }

        // 멱등 처리
        notification.markAsRead();

        return NotificationGetResponseDTO.from(notification);
    }

}
