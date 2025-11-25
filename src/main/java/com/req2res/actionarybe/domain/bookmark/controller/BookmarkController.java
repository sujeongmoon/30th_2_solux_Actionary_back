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
import com.req2res.actionarybe.domain.user.entity.User;
import com.req2res.actionarybe.domain.user.service.UserService;
import com.req2res.actionarybe.global.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookmarks")
public class BookmarkController {

	private final BookmarkService bookmarkService;
	private final UserService userService;

	@PostMapping
	public Response<BookmarkResponseDto> createBookmark(
		@AuthenticationPrincipal UserDetails userDetails,
		@RequestBody @Valid BookmarkRequestDto request
	) {
		User user = userService.findUserByLoginId(userDetails.getUsername());
		BookmarkResponseDto response = bookmarkService.createBookmark(user, request);
		return Response.success("북마크를 생성했습니다.", response);
	}

	@GetMapping
	public Response<BookmarkListResponseDto> getBookmarks(
		@AuthenticationPrincipal UserDetails userDetails
	) {
		User user = userService.findUserByLoginId(userDetails.getUsername());
		BookmarkListResponseDto response = bookmarkService.getBookmarks(user);
		return Response.success("북마크를 조회했습니다.", response);
	}

	@DeleteMapping("/{bookmarkId}")
	public Response<BookmarkListResponseDto> deleteBookmark(
		@AuthenticationPrincipal UserDetails userDetails,
		@PathVariable Long bookmarkId
	) {
		User user = userService.findUserByLoginId(userDetails.getUsername());
		bookmarkService.deleteBookmark(user, bookmarkId);
		return Response.success("북마크를 삭제했습니다.", null);
	}

}
