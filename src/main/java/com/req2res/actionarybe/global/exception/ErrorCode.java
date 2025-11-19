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
    LOGIN_ID_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다"),

    //auth
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"),
    INVALID_CONSTRAINT(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),

    INVALID_JSON(HttpStatus.BAD_REQUEST, "요청 JSON 형식이 올바르지 않습니다"),
    JSON_SYNTAX_ERROR(HttpStatus.BAD_REQUEST, "JSON 문법이 잘못되었습니다. (콤마, 중괄호, 따옴표 등을 확인하세요)"),
    INVALID_FIELD_TYPE(HttpStatus.BAD_REQUEST, "JSON 필드 타입이 올바르지 않습니다"),

    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다.");

	private final HttpStatus status;
	private final String message;

}
