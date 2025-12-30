package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.*;
import com.req2res.actionarybe.domain.todo.service.TodoService;
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

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;
    private final MemberRepository memberRepository;

    // 1. 투두 생성 API
    @Operation(
            summary = "투두 생성",
            description = "로그인한 사용자가 새로운 투두를 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투두 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "카테고리 없음(카테고리 지정 시)")
    })
    @PostMapping
    public Response<TodoCreateResponseDTO> createTodo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid TodoCreateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        TodoCreateResponseDTO data = todoService.createTodo(userId, request);

        return Response.success("투두가 생성되었습니다.", data);
    }

    // 2. 특정 날짜 투두 목록 조회 API
    /**
     * - 카테고리 설정: GET /api/todos?date=2025-10-31&categoryId=3
     * - 카테고리 미설정: GET /api/todos?date=2025-10-31
     */
    @Operation(
            summary = "특정 날짜 투두 목록 조회",
            description = "date(필수)와 categoryId(선택)를 이용해 특정 날짜의 투두 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투두 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(date 누락/형식 오류 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping
    public ResponseEntity<Response<DailyTodosResponseDTO>> getTodosByDate(
            @AuthenticationPrincipal UserDetails userDetails,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            @RequestParam(required = false)
            Long categoryId
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        DailyTodosResponseDTO data = todoService.getTodosByDate(userId, date, categoryId);

        return ResponseEntity.ok(
                Response.success("투두 목록 조회에 성공했습니다.", data)
        );
    }

    // 3. 투두 수정 API
    @Operation(
            summary = "투두 수정",
            description = "투두의 제목, 카테고리 등 일부 정보를 수정합니다. (요청 바디에서 제공된 값만 수정)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투두 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(수정할 내용 없음 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "투두 없음 또는 카테고리 없음(카테고리 변경 시)"),
            @ApiResponse(responseCode = "409", description = "이미 삭제된 투두 수정 시도(구현한 경우)")
    })
    @PatchMapping("/{todoId}")
    public ResponseEntity<Response<TodoResponseDTO>> updateTodo(
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        TodoResponseDTO data = todoService.updateTodo(userId, todoId, request);

        return ResponseEntity.ok(
                Response.success("투두가 수정되었습니다.", data)
        );
    }

    // 4. 투두 달성/실패 처리 API
    @Operation(
            summary = "투두 상태 변경(달성/실패)",
            description = "투두의 상태를 DONE 또는 FAILED로 변경합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투두 상태 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(유효하지 않은 상태 값 등)"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "투두 없음")
    })
    @PatchMapping("/{todoId}/status")
    public ResponseEntity<Response<TodoStatusResponseDTO>> updateTodoStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long todoId,
            @RequestBody TodoStatusUpdateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        TodoStatusResponseDTO data = todoService.updateTodoStatus(userId, todoId, request);

        String message = data.getStatus().equals("DONE")
                ? "할 일이 완료되었습니다."
                : "할 일이 실패로 처리되었습니다.";

        return ResponseEntity.ok(
                Response.success(message, data)
        );
    }

    // 5. 투두 삭제 API
    @Operation(
            summary = "투두 삭제",
            description = "로그인한 사용자가 본인의 투두를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "투두 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "투두 없음")
    })
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Response<Void>> deleteTodo(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long todoId
    ) {
        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        todoService.deleteTodo(userId, todoId);

        return ResponseEntity.ok(
                Response.success("투두가 삭제되었습니다.", null)
        );
    }
}
