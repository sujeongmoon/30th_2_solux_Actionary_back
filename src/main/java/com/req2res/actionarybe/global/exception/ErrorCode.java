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
	BOOKMARK_NOT_MATCH_MEMBER(HttpStatus.FORBIDDEN, "사용자의 북마크가 아닙니다."),

	//study
	STUDY_NOT_FIND(HttpStatus.NOT_FOUND, "존재하지 않는 스터디입니다."),
	STUDY_NOT_MATCH_MEMBER(HttpStatus.FORBIDDEN, "사용자가 방장인 스터디가 아닙니다."),
	STUDY_HAVE_USER(HttpStatus.CONFLICT, "스터디에 참여 중인 사용자가 있습니다."),
	STUDY_CREATE_ERROR(HttpStatus.BAD_GATEWAY, "Janus 서버와 통신에 실패했습니다."),
	STUDY_CAPACITY_EXCEEDED(HttpStatus.CONFLICT, "스터디 참여 인원이 이미 정원에 도달했습니다"),
	STUDY_PARTICIPANT_PASSWORD_REQUIRED(HttpStatus.FORBIDDEN, "비공개 스터디 입장 시 비밀번호가 필요합니다."),
	STUDY_PARTICIPANT_PASSWORD_UNREQUIRED(HttpStatus.BAD_REQUEST, "공개 스터디 입장 시 비밀번호가 필요하지 않습니다."),
	STUDY_PARTICIPANT_PASSWORD_BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "비공개 스터디 입장 비밀번호가 일치하지 않습니다."),
	STUDY_PARTICIPANT_NOT_JOINED(HttpStatus.FORBIDDEN, "유저가 해당 스터디에 참여하고 있지 않습니다."),
	STUDY_PARTICIPANT_ALREADY_JOINED(HttpStatus.CONFLICT, "유저가 이미 해당 스터디에 접속 중입니다."),

	//to-do
	TODO_INVALID_TITLE(HttpStatus.BAD_REQUEST, "할 일 제목은 비어 있을 수 없습니다."),
	TODO_INVALID_DATE(HttpStatus.BAD_REQUEST, "날짜 형식이 잘못되었습니다."),
	TODO_STATUS_CONFLICT(HttpStatus.CONFLICT, "이미 해당 상태로 처리된 투두입니다."),
	TODO_INVALID_STATUS(HttpStatus.BAD_REQUEST, "status 값은 DONE 또는 FAILED만 허용됩니다."),
	TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 투두입니다."),
	TODO_DELETE_CONFLICT(HttpStatus.CONFLICT, "이미 삭제되었거나 다른 엔티티에서 참조 중인 투두입니다."),

	//to-do-category
	TODO_CATEGORY_DUPLICATED(HttpStatus.CONFLICT, "같은 이름의 카테고리가 이미 존재합니다."),
	TODO_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 카테고리가 존재하지 않습니다."),
	TODO_CATEGORY_IN_USE(HttpStatus.CONFLICT, "해당 카테고리에 미완료 투두가 있어 삭제할 수 없습니다."),

	// point
	STUDY_TIME_POINT_ALREADY_EARNED_TODAY(HttpStatus.CONFLICT, "오늘은 이미 공부시간 포인트를 적립했습니다."),
	POINT_USER_MISMATCH(HttpStatus.FORBIDDEN, "요청 userId와 로그인 사용자가 일치하지 않습니다."),
	STUDY_PARTICIPATION_POINT_ALREADY_EARNED_TODAY(HttpStatus.CONFLICT, "해당 스터디 참여 포인트는 이미 적립되었습니다."),
	STUDY_PARTICIPATION_TIME_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "참여 시간은 30분 이상이어야 포인트가 적립됩니다."),
	TODO_POINT_ALREADY_EARNED(HttpStatus.CONFLICT, "이미 해당 투두 완료 포인트를 적립했습니다."),

	//search
	SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "검색 결과를 찾을 수 없습니다."),

	//user
	EMAIL_DUPLICATED(HttpStatus.CONFLICT, "중복된 이메일입니다"),
	MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
	BADGE_NOT_ASSIGNED(HttpStatus.NOT_FOUND, "해당 회원에게 할당된 뱃지가 없습니다."),
	LOGIN_ID_DUPLICATED(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다"),

	//auth
	BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다"),
	INVALID_CONSTRAINT(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),

	INVALID_JSON(HttpStatus.BAD_REQUEST, "요청 JSON 형식이 올바르지 않습니다"),
	JSON_SYNTAX_ERROR(HttpStatus.BAD_REQUEST, "JSON 문법이 잘못되었습니다. (콤마, 중괄호, 따옴표 등을 확인하세요)"),
	INVALID_FIELD_TYPE(HttpStatus.BAD_REQUEST, "JSON 필드 타입이 올바르지 않습니다"),

	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
	EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
	MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 존재하지 않습니다."),

    WITHDRAWN_MEMBER(HttpStatus.NOT_FOUND,"이미 탈퇴한 유저입니다."),

	// badge
	BADGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 뱃지입니다."),

	// post
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시물입니다."),

    // post comment
    POST_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다.");

	private final HttpStatus status;
	private final String message;

}
