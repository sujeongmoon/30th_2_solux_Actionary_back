package com.req2res.actionarybe.domain.study.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyParticipantUsersResponseDto {

	@Schema(description = "스터디 id", example = "1")
	private Long studyId;

	@Schema(description = "스터디에 접속한 본인을 나타내는 객체", example = "{\n"
		+ "      \"studyParticipantId\": 1,\n"
		+ "      \"userId\": 1,\n"
		+ "      \"userNickname\": \"눈송이\",\n"
		+ "      \"profileImageUrl\": \"noonsongprofile.jpg\",\n"
		+ "      \"badgeId\" : 1,\n"
		+ "      \"badgeImageUrl\": \"badge.jpg\"\n"
		+ "    }")
	private StudyParticipantUserDto me;

	@Schema(description = "스터디에 접속 중인 유저 목록", example = "[\n"
		+ "\t\t  {\n"
		+ "\t\t\t  \"studyParticipantId\" : 1,\n"
		+ "\t\t\t  \"userId\" : 1,\n"
		+ "\t\t\t  \"userNickname\" : \"눈송이\",\n"
		+ "\t\t\t  \"profileImageUrl\" : \"noonsongprofile.jpg\",\n"
		+ "\t\t\t  \"badgeId\" : 1,\n"
		+ "\t\t\t  \"badgeImageUrl\" : \"badge.jpg\"\n"
		+ "\t\t  },\n"
		+ "\t\t  {\n"
		+ "\t\t\t  \"studyParticipantId\" : 2,\n"
		+ "\t\t\t  \"userId\" : 2,\n"
		+ "\t\t\t  \"userNickname\" : \"쬬르디\",\n"
		+ "\t\t\t  \"profileImageUrl\" : \"jjordiprofile.jpg\",\n"
		+ "\t\t\t  \"badgeId\" : 1,\n"
		+ "\t\t\t  \"badgeImageUrl\" : \"badge.jpg\"\n"
		+ "\t\t\t}\n"
		+ "\t  ]")
	private List<StudyParticipantUserDto> participatingUsers;
}
