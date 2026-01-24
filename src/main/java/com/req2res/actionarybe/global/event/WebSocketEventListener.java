package com.req2res.actionarybe.global.event;

import com.req2res.actionarybe.domain.study.service.StudyParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final StudyParticipantService studyParticipantService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String participantIdStr = (String) accessor.getSessionAttributes().get("participantId");

        if (participantIdStr != null) {
            Long participantId = Long.parseLong(participantIdStr);
            studyParticipantService.updateStudyParticipantAuto(participantId);
        }
    }
}