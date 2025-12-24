package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.TodoCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.service.TodoService;
import com.req2res.actionarybe.domain.user.entity.User;
import com.req2res.actionarybe.domain.user.repository.UserRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.req2res.actionarybe.domain.todo.dto.category.TodoCategoryCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.category.TodoCategoryCreateResponseDTO;
import com.req2res.actionarybe.domain.todo.service.TodoCategoryService;
import com.req2res.actionarybe.global.Response;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todo-categories")
@Validated
public class TodoCategoryController {

    private final TodoService todoService;
    private final UserRepository userRepository;
    private final TodoCategoryService todoCategoryService;

    @PostMapping
    public ResponseEntity<Response<TodoCategoryCreateResponseDTO>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid TodoCategoryCreateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();
        TodoCategoryCreateResponseDTO data = todoCategoryService.createCategory(userId, request);
        return ResponseEntity.ok(Response.success("카테고리가 생성되었습니다.", data));
    }
}

