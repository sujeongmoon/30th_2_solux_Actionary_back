package com.req2res.actionarybe.domain.study.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ParticipantJoinedEvent {

	private Long studyParticipantId;
	private Long studyId;
	private Long userId;
	private String userNickname;
	private String profileImageUrl;
	private Long badgeId;
	private String badgeImageUrl;

}
