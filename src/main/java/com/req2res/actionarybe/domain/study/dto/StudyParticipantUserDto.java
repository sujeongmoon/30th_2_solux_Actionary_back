package com.req2res.actionarybe.domain.study.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyParticipantUserDto {

	@Schema(description = "스터디 참여 id", example = "1")
	private Long studyParticipantId;

	@Schema(description = "스터디에 접속한 유저 id", example = "1")
	private Long userId;

	@Schema(description = "스터디에 접속한 유저 닉네임", example = "눈송이")
	private String userNickname;

	@Schema(description = "스터디에 접속한 유저 프로필 사진 url", example = "noonsongprofile.jpg")
	private String profileImageUrl;

	@Schema(description = "스터디에 접속한 유저 뱃지 id", example = "1")
	private Long badgeId;

	@Schema(description = "스터디에 접속한 유저 뱃지 사진 url", example = "badge.jpg")
	private String badgeImageUrl;

}
