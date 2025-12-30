package com.req2res.actionarybe.domain.study.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.req2res.actionarybe.domain.study.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StudyListResponseDto {

	@JsonProperty("isPublic")
	@Schema(description = "스터디 공개 여부", example = "false")
	private Boolean isPublic;

	@Schema(description = "스터디 카테고리", example = "TEACHER_EXAM")
	private Category category;

	@Schema(description = "스터디 카테고리 라벨명", example = "임용")
	private String categoryLabel;

	@Schema(description = "각 스터디 정보를 알려주는 배열", example = "[\n"
		+ "\t\t  {\n"
		+ "\t\t\t  \"studyId\": 1,\n"
		+ "\t\t\t  \"studyName\" : \"자바 공부방\",\n"
		+ "\t\t\t  \"coverImage\" : \"cover_image.jpg\"\n"
		+ "\t\t\t}, ...\n"
		+ "\t\t\t{\n"
		+ "\t\t\t  \"studyId\": 8,\n"
		+ "\t\t\t  \"studyName\" : \"토익 900점 목표방\",\n"
		+ "\t\t\t  \"coverImage\" : \"cover_image.jpg\"\n"
		+ "\t\t\t]\t  \n"
		+ "\t  ]")
	private List<StudySummaryDto> content;

	@Schema(description = "현재 페이지", example = "0")
	private int page;

	@Schema(description = "한 페이지에 보여질 스터디 수", example = "8")
	private int size;

	@Schema(description = "전체 스터디 개수", example = "10")
	private long totalElements;

	@Schema(description = "전체 페이지 수", example = "2")
	private int totalPages;
}
