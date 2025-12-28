package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.category.*;
import com.req2res.actionarybe.domain.todo.service.TodoCategoryService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo-categories")
@Validated
public class TodoCategoryController {

    private final MemberRepository memberRepository;
    private final TodoCategoryService todoCategoryService;

    // 1. 카테고리 생성 API
    @Operation(
            summary = "투두 카테고리 생성",
            description = "로그인한 사용자가 새로운 투두 카테고리를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "중복된 카테고리 이름")
    })
    @PostMapping
    public ResponseEntity<Response<TodoCategoryCreateResponseDTO>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid TodoCategoryCreateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();
        TodoCategoryCreateResponseDTO data = todoCategoryService.createCategory(userId, request);

        return ResponseEntity.ok(Response.success("카테고리가 생성되었습니다.", data));
    }

    // 2. 카테고리 수정 API
    @Operation(
            summary = "투두 카테고리 수정",
            description = "카테고리 이름 또는 색상을 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정할 값이 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @PatchMapping("/{categoryId}")
    public ResponseEntity<Response<TodoCategoryUpdateResponseDTO>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId,
            @RequestBody TodoCategoryUpdateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        TodoCategoryUpdateResponseDTO data =
                todoCategoryService.updateCategory(userId, categoryId, request);

        return ResponseEntity.ok(Response.success("카테고리가 수정되었습니다.", data));
    }

    // 3. 카테고리 삭제 API
    @Operation(
            summary = "투두 카테고리 삭제",
            description = "미완료(PENDING, FAILED) 투두가 없는 경우에만 카테고리를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음"),
            @ApiResponse(responseCode = "409", description = "미완료 투두가 존재하여 삭제 불가")
    })
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Response<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        todoCategoryService.deleteCategory(userId, categoryId);

        return ResponseEntity.ok(Response.success("카테고리가 삭제되었습니다.", null));
    }

    // 4. 카테고리 목록 조회 API
    @Operation(
            summary = "투두 카테고리 목록 조회",
            description = "로그인한 사용자가 보유한 모든 투두 카테고리를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<Response<List<TodoCategoryListItemDTO>>> getMyCategories(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        List<TodoCategoryListItemDTO> data = todoCategoryService.getCategory(userId);

        return ResponseEntity.ok(Response.success("", data));
    }
}
