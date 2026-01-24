package com.req2res.actionarybe.domain.bookmark.dto;

import com.req2res.actionarybe.domain.bookmark.entity.Bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookmarkResponseDto {

	@Schema(description = "북마크 id", example = "1")
	private Long bookmarkId;

	@Schema(description = "북마크 이름", example = "인프런")
	private String bookmarkName;

	@Schema(description = "북마크 링크", example = "https://www.inflearn.com/ko/?NaPm=ct%3Dmhkerxmz%7Cci%3Dcheckout%7Ctr%3Dds%7Ctrx%3Dnull%7Chk%3Dcb7d60439347d96b150a886395bc057952358773")
	private String link;

	public static BookmarkResponseDto from(Bookmark bookmark) {
		return BookmarkResponseDto.builder()
			.bookmarkId(bookmark.getId())
			.bookmarkName(bookmark.getName())
			.link(bookmark.getLink())
			.build();
	}
}
