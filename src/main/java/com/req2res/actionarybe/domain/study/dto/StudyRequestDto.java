package com.req2res.actionarybe.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.req2res.actionarybe.domain.study.entity.Category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StudyRequestDto {

	@NotBlank(message = "스터디 이름은 비어있을 수 없습니다.")
	@Size(min = 1, max = 20, message = "스터디 이름은 20자 이내여야 합니다.")
	@Schema(description = "스터디 이름", example = "06-12 임용송이 오전 공부반")
	private String studyName;

	@Schema(description = "스터디 커버 사진", example = "image_url.jpg")
	private String coverImage;

	@NotNull(message = "스터디 카테고리는 비어있을 수 없습니다.")
	@Schema(description = "스터디 카테고리", example = "TEACHER_EXAM")
	private Category category;

	@Size(min = 1, max = 20, message = "스터디 소개글은 20자 이내여야 합니다.")
	@Schema(description = "스터디 간단 소개글", example = "눈송이 인증 받아요")
	private String description;

	@Min(value = 2, message = "인원 제한은 최소 2명 이상이어야 합니다.")
	@Schema(description = "스터디 인원제한", example = "15")
	private int memberLimit;

	@JsonProperty("isPublic")
	@NotNull(message = "스터디 공개 여부는 비어있을 수 없습니다.")
	@Schema(description = "스터디 공개 여부", example = "false")
	private Boolean isPublic;

	@Pattern(regexp = "^[0-9]{6}$", message = "비밀번호는 숫자 6자리여야 합니다.")
	@Schema(description = "스터디 비밀번호", example = "060522")
	private String password;

	@AssertTrue(message = "공개 스터디는 비밀번호가 없어야 하고, 비공개 스터디는 비밀번호가 필요합니다.")
	public boolean passwordCheck() {
		if (Boolean.TRUE.equals(isPublic)) {
			return password == null || password.isBlank();
		}
		return password != null;
	}
}
