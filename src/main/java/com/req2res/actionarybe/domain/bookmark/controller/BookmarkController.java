package com.req2res.actionarybe.domain.bookmark.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.req2res.actionarybe.domain.bookmark.dto.BookmarkListResponseDto;
import com.req2res.actionarybe.domain.bookmark.dto.BookmarkRequestDto;
import com.req2res.actionarybe.domain.bookmark.dto.BookmarkResponseDto;
import com.req2res.actionarybe.domain.bookmark.service.BookmarkService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.global.Response;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

	private final BookmarkService bookmarkService;
	private final MemberService memberService;

	@Operation(summary = "북마크 생성 API", description = "북마크를 생성하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "북마크 생성 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "북마크를 생성했습니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "북마크 링크가 비어있는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "북마크 링크는 비어있을 수 없습니다."
				}
				"""))
		)
	})
	@PostMapping
	public Response<BookmarkResponseDto> createBookmark(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid BookmarkRequestDto request
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		BookmarkResponseDto response = bookmarkService.createBookmark(member, request);
		return Response.success("북마크를 생성했습니다.", response);
	}

	@Operation(summary = "북마크 목록 조회 API", description = "북마크 목록을 조회하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "북마크 목록 조회 완료",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 200,
					"message": "북마크를 조회했습니다."
				}
				"""))
		)
	})
	@GetMapping
	public Response<BookmarkListResponseDto> getBookmarks(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		BookmarkListResponseDto response = bookmarkService.getBookmarks(member);
		return Response.success("북마크를 조회했습니다.", response);
	}

	@Operation(summary = "북마크 삭제 API", description = "북마크를 삭제하는 기능입니다.")
	@ApiResponses(value = {
		@ApiResponse(
			responseCode = "200",
			description = "북마크를 삭제했습니다."
		),
		@ApiResponse(
			responseCode = "400",
			description = "사용자의 북마크가 아닌 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 400,
					"message": "사용자의 북마크가 아닙니다."
				}
				"""))
		),
		@ApiResponse(
			responseCode = "404",
			description = "북마크가 존재하지 않는 경우",
			content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
				{
					"code": 404,
					"message": "존재하지 않는 북마크입니다."
				}
				"""))
		)
	})
	@DeleteMapping("/{bookmarkId}")
	public Response<BookmarkListResponseDto> deleteBookmark(
		@AuthenticationPrincipal UserDetails userDetails,
		@Parameter(name = "bookmarkId", description = "조회할 북마크의 ID", example = "1")
		@PathVariable Long bookmarkId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		bookmarkService.deleteBookmark(member, bookmarkId);
		return Response.success("북마크를 삭제했습니다.", null);
	}

}
