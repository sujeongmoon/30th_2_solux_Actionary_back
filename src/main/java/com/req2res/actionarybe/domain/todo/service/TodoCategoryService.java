package com.req2res.actionarybe.domain.todo.service;

import com.req2res.actionarybe.domain.todo.dto.category.*;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import com.req2res.actionarybe.domain.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.req2res.actionarybe.domain.todo.entity.TodoCategory;
import com.req2res.actionarybe.domain.todo.repository.TodoCategoryRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoCategoryService {

    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;

    //1) 카테고리 생성
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

    //2) 카테고리 수정
    @Transactional
    public TodoCategoryUpdateResponseDTO updateCategory(Long userId, Long categoryId,
                                                        TodoCategoryUpdateRequestDTO request) {

        // 400: 둘 다 비어있을 때
        boolean isNameEmpty = (request.getName() == null || request.getName().isBlank());
        boolean isColorEmpty = (request.getColor() == null || request.getColor().isBlank());
        if (isNameEmpty && isColorEmpty) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "수정할 내용이 없습니다.");
        }

        // 404: 존재하지 않거나 (또는 내 카테고리가 아닌 경우)
        TodoCategory category = todoCategoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_CATEGORY_NOT_FOUND));


        // name 수정 (값이 들어온 경우만)
        if (!isNameEmpty) {
            // 중복 이름 방지(본인 카테고리 내)
            if (!request.getName().equals(category.getName())
                    && todoCategoryRepository.existsByUserIdAndName(userId, request.getName())) {
                throw new CustomException(ErrorCode.TODO_CATEGORY_DUPLICATED);
            }
            category.updateName(request.getName());
        }

        // color 수정 (값이 들어온 경우만)
        if (!isColorEmpty) {
            category.updateColor(request.getColor());
        }

        return new TodoCategoryUpdateResponseDTO(
                category.getId(),
                category.getName(),
                category.getColor()
        );
    }

    //3) 투두 카테고리 삭제 API
    @Transactional
    public void deleteCategory(Long userId, Long categoryId) {

        TodoCategory category = todoCategoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_CATEGORY_NOT_FOUND));

        boolean hasBlockingTodo = todoRepository.existsByCategoryIdAndStatuses(
                userId,
                categoryId,
                List.of(Todo.Status.PENDING, Todo.Status.FAILED)
        );

        if (hasBlockingTodo) {
            throw new CustomException(ErrorCode.TODO_CATEGORY_IN_USE,
                    "해당 카테고리에 미완료(PENDING/FAILED) 투두가 있어 삭제할 수 없습니다.");
        }

        todoCategoryRepository.delete(category);
    }

    //4) 카테고리 목록 조회
    @Transactional(readOnly = true)
    public List<TodoCategoryListItemDTO> getCategory(Long userId) {

        List<TodoCategory> categories = todoCategoryRepository.findAllByUserIdOrderByCreatedAtAsc(userId);

        return categories.stream()
                .map(c -> new TodoCategoryListItemDTO(
                        c.getId(),
                        c.getName(),
                        c.getColor(),
                        c.getCreatedAt()
                ))
                .toList();
    }

}

