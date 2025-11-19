package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.DailyTodosResponseDTO;
import com.req2res.actionarybe.domain.todo.dto.TodoCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.TodoCreateResponseDTO;
import com.req2res.actionarybe.domain.todo.service.TodoService;
import com.req2res.actionarybe.global.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    //투두 생성 API
    @PostMapping
    public Response<TodoCreateResponseDTO> createTodo(
            // 원래는 토큰에서 사용자 정보 꺼내야 함
            // @RequestHeader("Authorization") String authorization,
            // @AuthenticationPrincipal User user,
            @RequestBody @Valid TodoCreateRequestDTO request
    ) {

        // TODO: 로그인 기능 구현 후 아래 부분을 실제 유저 정보로 교체
        // Long userId = user.getId();
        Long userId = 1L; // 임시 유저 ID (테스트용)

        TodoCreateResponseDTO data = todoService.createTodo(userId, request);

        return Response.success("투두가 생성되었습니다.", data);
    }

    //특정 날짜 투두 목록 조회 API
    /**
     * - 카테고리 설정: GET /api/todos?date=2025-10-31&categoryId=3
     * - 카테고리 미설정: GET /api/todos?date=2025-10-31
     *
     * 지금은 인증 기능 미완성이라 토큰 사용 부분은 주석 처리해둠.
     */
    @GetMapping
    public ResponseEntity<Response<DailyTodosResponseDTO>> getTodosByDate(
            // TODO: 로그인 연동 후 주석 해제
            // @AuthenticationPrincipal CustomUserDetails userDetails,

            // ?date=2025-10-31 형식으로 들어오는 쿼리 파라미터
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            // ?categoryId=3 (선택 값)
            @RequestParam(required = false)
            Long categoryId
    ) {
        // TODO: 토큰 붙이면 여기서 userId 꺼내서 서비스에 넘기면 됨
        // Long userId = userDetails.getId();

        DailyTodosResponseDTO data = todoService.getTodosByDate(date, categoryId);

        return ResponseEntity.ok(
                Response.success("투두 목록 조회에 성공했습니다.", data)
        );
    }
}

