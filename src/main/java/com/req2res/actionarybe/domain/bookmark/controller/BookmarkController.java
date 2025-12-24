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

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

	private final BookmarkService bookmarkService;
	private final MemberService memberService;

	@PostMapping
	public Response<BookmarkResponseDto> createBookmark(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid BookmarkRequestDto request
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		BookmarkResponseDto response = bookmarkService.createBookmark(member, request);
		return Response.success("북마크를 생성했습니다.", response);
	}

	@GetMapping
	public Response<BookmarkListResponseDto> getBookmarks(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		BookmarkListResponseDto response = bookmarkService.getBookmarks(member);
		return Response.success("북마크를 조회했습니다.", response);
	}

	@DeleteMapping("/{bookmarkId}")
	public Response<BookmarkListResponseDto> deleteBookmark(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long bookmarkId
	) {
		Member member = memberService.findMemberByLoginId(userDetails.getUsername());
		bookmarkService.deleteBookmark(member, bookmarkId);
		return Response.success("북마크를 삭제했습니다.", null);
	}

}
