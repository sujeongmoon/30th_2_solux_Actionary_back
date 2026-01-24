package com.req2res.actionarybe.domain.study.dto.event;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestEvent {

	private Long senderId;
	private String chat;

}
