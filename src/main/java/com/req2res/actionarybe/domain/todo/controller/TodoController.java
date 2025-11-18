package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.TodoCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.TodoCreateResponseDTO;
import com.req2res.actionarybe.domain.todo.service.TodoService;
import com.req2res.actionarybe.global.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


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
}

