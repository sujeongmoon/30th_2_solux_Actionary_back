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
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TodoCategoryService {

    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;

    // 프론트 팔레트 고정값 (대문자 통일)
    private static final Set<String> ALLOWED_COLORS = Set.of(
            "#D29AFA", "#6BEBFF", "#9AFF5B", "#FFAD36",
            "#FF8355", "#FCDF2F", "#FF3D2F", "#FF9E97"
    );

    private void validatePaletteColor(String color) {
        if (color == null || color.isBlank()) return; // 색 안 보내면 검증 패스
        String normalized = color.trim().toUpperCase();

        // 1) 형식 검증 (# + 6자리 hex)
        if (!normalized.matches("^#[0-9A-F]{6}$")) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "color 형식이 올바르지 않습니다. 예: #D29AFA");
        }

        // 2) 팔레트 포함 여부 검증
        if (!ALLOWED_COLORS.contains(normalized)) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "허용되지 않은 color 값입니다.");
        }
    }


    //1) 카테고리 생성
    @Transactional
    public TodoCategoryCreateResponseDTO createCategory(Long userId,
                                                        TodoCategoryCreateRequestDTO request) {


        // 409: 같은 유저 내 동일 이름 카테고리 존재
        if (todoCategoryRepository.existsByUserIdAndName(userId, request.getName())) {
            throw new CustomException(ErrorCode.TODO_CATEGORY_DUPLICATED);
        }

        validatePaletteColor(request.getColor());

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
            validatePaletteColor(request.getColor());
            category.updateColor(request.getColor().trim().toUpperCase());
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

