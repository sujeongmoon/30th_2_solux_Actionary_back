package com.req2res.actionarybe.global.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// Basic HttpStatusCode
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "BAD REQUEST"),
	FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN"),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED"),
	NOT_FOUND(HttpStatus.NOT_FOUND, "NOT FOUND"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL SERVER ERROR"),

	// 각 Service에서 필요한 ErrorCode 추가

	//user
	EMAIL_DUPLICATED(HttpStatus.CONFLICT, "중복된 이메일입니다"),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "Refresh Token이 유효하지 않습니다.");

	private final HttpStatus status;
	private final String message;

}
