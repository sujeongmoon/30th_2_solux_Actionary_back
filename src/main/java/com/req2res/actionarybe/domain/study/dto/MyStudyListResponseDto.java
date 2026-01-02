package com.req2res.actionarybe.domain.study.dto;

import java.util.List;

import com.req2res.actionarybe.domain.study.entity.Scope;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MyStudyListResponseDto {

	@Schema(description = "나만의 스터디 범위", example = "ALL")
	private Scope scope;

	@Schema(description = "나만의 스터디 범위 라벨명", example = "전체")
	private String scopeLabel;

	@Schema(description = "각 스터디 정보를 알려주는 배열", example = "[\n"
		+ "\t\t  {\n"
		+ "\t\t\t  \"studyId\": 1,\n"
		+ "\t\t\t  \"name\" : \"자바 공부방\",\n"
		+ "\t\t\t  \"coverImage\" : \"cover_image.jpg\",\n"
		+ "\t\t\t  \"description\" : \"자바를 공부하는 방입니다.\",\n"
		+ "\t\t\t  \"memberNow\" : \"5\"\n"
		+ "\t\t\t  \"\n"
		+ "\t\t\t}, ...\n"
		+ "\t\t\t{\n"
		+ "\t\t\t  \"studyId\": 5,\n"
		+ "\t\t\t  \"name\" : \"토익 900점 목표방\",\n"
		+ "\t\t\t  \"coverImage\" : \"cover_image.jpg\",\n"
		+ "\t\t\t  \"description\" : \"토익900점가자아아앗\",\n"
		+ "\t\t\t  \"memberNow\" : \"5\"\n"
		+ "\t\t\t}\t  \n"
		+ "\t  ]")
	private List<StudyInteractionSummaryDto> content;

	@Schema(description = "현재 페이지", example = "0")
	private int page;

	@Schema(description = "한 페이지에 보여질 스터디 수", example = "3")
	private int size;

	@Schema(description = "전체 스터디 개수", example = "10")
	private long totalElements;

	@Schema(description = "전체 페이지 수", example = "2")
	private int totalPages;
}
