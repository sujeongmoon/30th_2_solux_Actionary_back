package com.req2res.actionarybe.domain.notification.service;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateRequestDTO;
import com.req2res.actionarybe.domain.notification.dto.NotificationCreateResponseDTO;
import com.req2res.actionarybe.domain.notification.entity.Notification;
import com.req2res.actionarybe.domain.notification.repository.NotificationRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;

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
}
