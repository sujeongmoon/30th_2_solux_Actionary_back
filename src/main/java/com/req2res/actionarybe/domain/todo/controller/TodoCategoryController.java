package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.todo.dto.TodoCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.category.TodoCategoryUpdateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.category.TodoCategoryUpdateResponseDTO;
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

    //1. 카테고리 생성 API
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

    //2. 카테고리 수정 API
    @PatchMapping("/{categoryId}")
    public ResponseEntity<Response<TodoCategoryUpdateResponseDTO>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId,
            @RequestBody TodoCategoryUpdateRequestDTO request
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();

        TodoCategoryUpdateResponseDTO data = todoCategoryService.updateCategory(userId,
                categoryId, request);
        return ResponseEntity.ok(Response.success("카테고리가 수정되었습니다.", data));
    }

    //3. 카테고리 삭제 API
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Response<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long categoryId
    ) {
        String loginId = userDetails.getUsername();

        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = user.getId();

        todoCategoryService.deleteCategory(userId, categoryId);

        return ResponseEntity.ok(Response.success("카테고리가 삭제되었습니다.", null));
    }
}

