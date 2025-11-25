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

	// bookmark
	BOOKMARK_LINK_NOT_FOUND(HttpStatus.NOT_FOUND, "북마크 링크는 비어있을 수 없습니다."),
	BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 북마크입니다."),
	BOOKMARK_NOT_MATCH_MEMBER(HttpStatus.BAD_REQUEST, "사용자의 북마크가 아닙니다."),

	//todo
	TODO_INVALID_TITLE(HttpStatus.BAD_REQUEST, "할 일 제목은 비어 있을 수 없습니다."),
	TODO_INVALID_DATE(HttpStatus.BAD_REQUEST, "날짜 형식이 잘못되었습니다."),
	TODO_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
	TODO_STATUS_CONFLICT(HttpStatus.CONFLICT, "이미 해당 상태로 처리된 투두입니다."),
	TODO_INVALID_STATUS(HttpStatus.BAD_REQUEST, "status 값은 DONE 또는 FAILED만 허용됩니다."),
	TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 투두입니다."),
	TODO_DELETE_CONFLICT(HttpStatus.CONFLICT, "이미 삭제되었거나 다른 엔티티에서 참조 중인 투두입니다."),

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
