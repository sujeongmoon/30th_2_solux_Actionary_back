package com.req2res.actionarybe.domain.notification.service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateRequestDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateResponseDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationGetResponseDTO;
import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.repository.NotificationRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

    // 1. 알림 생성 API
    //직접 HTTP 호출이 아니라 다른 API에서 .create로 호출하는 형식 사용
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
