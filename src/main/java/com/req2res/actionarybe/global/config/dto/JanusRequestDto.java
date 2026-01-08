package com.req2res.actionarybe.global.config.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JanusRequestDto {
	private String janus;
	private String transaction;
	private String plugin;
	private Object body;
}
