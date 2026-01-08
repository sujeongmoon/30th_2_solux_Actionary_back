package com.req2res.actionarybe.global.config.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JanusResponse<T> {
	private String janus;
	private T data;

	@Getter
	@NoArgsConstructor
	public static class Data {
		private Long id;
	}
}
