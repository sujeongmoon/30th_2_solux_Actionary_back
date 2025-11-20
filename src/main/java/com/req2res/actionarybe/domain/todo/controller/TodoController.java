package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.*;
import com.req2res.actionarybe.domain.todo.service.TodoService;
import com.req2res.actionarybe.domain.user.entity.User;
import com.req2res.actionarybe.domain.user.repository.UserRepository;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
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
    private final UserRepository userRepository;

    //1. 투두 생성 API
    @PostMapping
    public Response<TodoCreateResponseDTO> createTodo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid TodoCreateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();

        TodoCreateResponseDTO data = todoService.createTodo(userId, request);

        return Response.success("투두가 생성되었습니다.", data);
    }

    //2. 특정 날짜 투두 목록 조회 API
    /**
     * - 카테고리 설정: GET /api/todos?date=2025-10-31&categoryId=3
     * - 카테고리 미설정: GET /api/todos?date=2025-10-31
     */
    @GetMapping
    public ResponseEntity<Response<DailyTodosResponseDTO>> getTodosByDate(
            @AuthenticationPrincipal UserDetails userDetails,

            // ?date=2025-10-31 형식으로 들어오는 쿼리 파라미터
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,

            // ?categoryId=3 (선택 값)
            @RequestParam(required = false)
            Long categoryId
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();

        DailyTodosResponseDTO data = todoService.getTodosByDate(userId, date, categoryId);

        return ResponseEntity.ok(
                Response.success("투두 목록 조회에 성공했습니다.", data)
        );
    }

    //3. 투두 수정 API
    @PatchMapping("/{todoId}")
    public ResponseEntity<Response<TodoResponseDTO>> updateTodo(
            @PathVariable Long todoId,
            @RequestBody TodoUpdateRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();

        TodoResponseDTO data = todoService.updateTodo(userId, todoId, request);

        return ResponseEntity.ok(
                Response.success("투두가 수정되었습니다.", data)
        );
    }

    // 4. 투두 달성/실패 처리 API
    @PatchMapping("/{todoId}/status")
    public ResponseEntity<Response<TodoStatusResponseDTO>> updateTodoStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long todoId,
            @RequestBody TodoStatusUpdateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();

        TodoStatusResponseDTO data = todoService.updateTodoStatus(userId, todoId, request);

        String message = data.getStatus().equals("DONE")
                ? "할 일이 완료되었습니다."
                : "할 일이 실패로 처리되었습니다.";

        return ResponseEntity.ok(
                Response.success(message, data)
        );
    }

    //5. 투두 삭제 API
    @DeleteMapping("/{todoId}")
    public ResponseEntity<Response<Void>> deleteTodo(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long todoId)
    {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();

        // 3) 서비스에 userId까지 넘겨서 “본인 투두인지 검증 후 삭제”
        todoService.deleteTodo(userId, todoId);

        return ResponseEntity.ok(
                Response.success("투두가 삭제되었습니다.", null)
        );
    }
}

