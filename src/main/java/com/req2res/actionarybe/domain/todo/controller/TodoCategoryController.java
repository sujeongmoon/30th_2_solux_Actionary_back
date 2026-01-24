package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.category.*;
import com.req2res.actionarybe.domain.todo.service.TodoCategoryService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo-categories")
@Validated
@SecurityRequirement(name = "Bearer Token")
public class TodoCategoryController {

    private final MemberRepository memberRepository;
    private final TodoCategoryService todoCategoryService;

    // 1. 카테고리 생성 API
    @Operation(
            summary = "투두 카테고리 생성",
            description = "로그인한 사용자가 새로운 투두 카테고리를 생성합니다.색상은 \\\"#D29AFA\\\", \\\"#6BEBFF\\\", " +
                    "\\\"#9AFF5B\\\", \\\"#FFAD36\\\",\\\"#FF8355\\\", \\\"#FCDF2F\\\", \\\"#FF3D2F\\\", \\\"#FF9E97\\\"중에만 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "409", description = "중복된 카테고리 이름")
    })
    @PostMapping
    public ResponseEntity<Response<TodoCategoryCreateResponseDTO>> create(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "카테고리 생성 요청 정보", required = true)
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
            description = "카테고리 이름 또는 색상을 수정합니다.색상은 \\\"#D29AFA\\\", \\\"#6BEBFF\\\", " +
                    "\\\"#9AFF5B\\\", \\\"#FFAD36\\\",\\\"#FF8355\\\", \\\"#FCDF2F\\\", \\\"#FF3D2F\\\", \\\"#FF9E97\\\"중에만 가능합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "수정할 값이 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음")
    })
    @PatchMapping("/{categoryId}")
    public ResponseEntity<Response<TodoCategoryUpdateResponseDTO>> update(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "수정할 카테고리 ID", example = "1")
            @PathVariable Long categoryId,

            @Parameter(description = "카테고리 수정 요청 정보")
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
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "삭제할 카테고리 ID", example = "1")
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
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(
                    description = "조회 기준 날짜 (이 날짜 기준 startDate <= date 인 카테고리만 반환). 미입력 시 오늘 날짜 기준",
                    example = "2026-01-20"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        // date가 null이면 서비스에서 today로 처리
        List<TodoCategoryListItemDTO> data = todoCategoryService.getCategory(userId, date);

        return ResponseEntity.ok(Response.success("카테고리 목록 조회 성공", data));
    }
}

