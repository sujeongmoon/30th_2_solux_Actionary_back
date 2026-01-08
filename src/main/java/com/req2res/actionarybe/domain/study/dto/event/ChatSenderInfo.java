package com.req2res.actionarybe.domain.study.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChatSenderInfo {

	Long studyParticipantId;
	Long senderId;
	String senderNickname;
	Long badgeId;
	String badgeImageUrl;

}
