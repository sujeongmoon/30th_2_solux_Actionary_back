package com.req2res.actionarybe.domain.bookmark.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BookmarkRequestDto {

	@Schema(description = "북마크 이름", example = "인프런")
	private String bookmarkName;

	@Schema(description = "북마크 링크", example = "https://www.inflearn.com/ko/?NaPm=ct%3Dmhkerxmz%7Cci%3Dcheckout%7Ctr%3Dds%7Ctrx%3Dnull%7Chk%3Dcb7d60439347d96b150a886395bc057952358773")
	private String link;

}
