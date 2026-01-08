package com.req2res.actionarybe.global.config;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.req2res.actionarybe.global.config.dto.JanusRequestDto;
import com.req2res.actionarybe.global.config.dto.JanusResponse;

@Component
public class JanusClient {

	private final WebClient webClient;

	public JanusClient(@Value("${janus.base-url}") String baseUrl) {
		this.webClient = WebClient.builder().baseUrl(baseUrl).build();
	}

	public Long createStudyRoom(Long roomId) {
		Long sessionId = createSession();
		Long handleId = attachVideoRoomPlugin(sessionId);
		sendCreateRoomCommand(sessionId, handleId, roomId);

		return roomId;
	}

	private Long createSession() {
		var request = JanusRequestDto.builder()
			.janus("create")
			.transaction(generateTx())
			.build();

		return postRequest(request, "")
			.getData().getId();
	}

	private Long attachVideoRoomPlugin(Long sessionId) {
		var request = JanusRequestDto.builder()
			.janus("attach")
			.plugin("janus.plugin.videoroom")
			.transaction(generateTx())
			.build();

		return postRequest(request, "/" + sessionId)
			.getData().getId();
	}

	private void sendCreateRoomCommand(Long sessionId, Long handleId, Long roomId) {
		var body = Map.of(
			"request", "create",
			"room", roomId,
			"permanent", true
		);
		var request = JanusRequestDto.builder()
			.janus("message")
			.transaction(generateTx())
			.body(body)
			.build();

		postRequest(request, "/" + sessionId + "/" + handleId);
	}

	private JanusResponse<JanusResponse.Data> postRequest(Object body, String uri) {
		return webClient.post()
			.uri(uri)
			.bodyValue(body)
			.retrieve()
			.bodyToMono(new ParameterizedTypeReference<JanusResponse<JanusResponse.Data>>() {
			})
			.block();
	}

	private String generateTx() {
		return UUID.randomUUID().toString();
	}
}
