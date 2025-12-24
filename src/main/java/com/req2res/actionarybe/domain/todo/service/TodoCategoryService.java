package com.req2res.actionarybe.domain.todo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.req2res.actionarybe.domain.todo.dto.category.TodoCategoryCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.category.TodoCategoryCreateResponseDTO;
import com.req2res.actionarybe.domain.todo.entity.TodoCategory;
import com.req2res.actionarybe.domain.todo.repository.TodoCategoryRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoCategoryService {

    private final TodoCategoryRepository todoCategoryRepository;

    @Transactional
    public TodoCategoryCreateResponseDTO createCategory(Long userId,
                                                        TodoCategoryCreateRequestDTO request) {

        // 409: 같은 유저 내 동일 이름 카테고리 존재
        if (todoCategoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new CustomException(ErrorCode.TODO_CATEGORY_DUPLICATED);
        }

        // TODO: 색상 팔레트 검증은 나중에 프론트 팔레트가 확정되면 여기서 체크하면 됨.
        // 지금은 "프론트에서 보내는 값을 그대로 받는다"라서 통과시키는 게 자연스러움.

        TodoCategory category = TodoCategory.builder()
                .userId(userId)
                .name(request.getName())
                .color(request.getColor())
                .build();

        TodoCategory saved = todoCategoryRepository.save(category);

        return new TodoCategoryCreateResponseDTO(
                saved.getId(),
                saved.getName(),
                saved.getColor(),
                saved.getCreatedAt()
        );
    }
}

