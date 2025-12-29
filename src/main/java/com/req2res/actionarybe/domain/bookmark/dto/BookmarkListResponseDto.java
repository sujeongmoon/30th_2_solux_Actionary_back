package com.req2res.actionarybe.domain.bookmark.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.req2res.actionarybe.domain.bookmark.entity.Bookmark;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BookmarkListResponseDto {

	@Schema(description = "북마크 목록")
	List<BookmarkResponseDto> bookmarks;

	public static BookmarkListResponseDto from(List<Bookmark> bookmarks) {
		List<BookmarkResponseDto> bookmarkResponseDtos = bookmarks.stream()
			.map(BookmarkResponseDto::from)
			.collect(Collectors.toList());

		return BookmarkListResponseDto.builder()
			.bookmarks(bookmarkResponseDtos)
			.build();
	}
}
