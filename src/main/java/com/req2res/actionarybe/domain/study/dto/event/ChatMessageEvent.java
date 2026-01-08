package com.req2res.actionarybe.domain.study.dto.event;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageEvent {

	private Long studyParticipantId;
	private Long studyId;
	private Long senderId;
	private String senderNickname;
	private Long badgeId;
	private String badgeImageUrl;
	private String chat;
	private LocalDateTime createdAt;

}
