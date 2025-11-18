package com.req2res.actionarybe.domain.todo.service;


import com.req2res.actionarybe.domain.todo.dto.TodoCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.TodoCreateResponseDTO;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import com.req2res.actionarybe.domain.todo.repository.TodoRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    //투두 생성 API
    @Transactional
    public TodoCreateResponseDTO createTodo(Long userId, TodoCreateRequestDTO request) {

        //title 항목 누락
        if (request.getTitle().isBlank()) {
            throw new CustomException(ErrorCode.TODO_INVALID_TITLE);
        }

        //date 항목 누락
        if (request.getDate() == null) {
            throw new CustomException(ErrorCode.TODO_INVALID_DATE);
        }

        // TODO: 추후에 투두카테고리 레포지 생성하고, 주석 풀기
        //해당 CategoryId가 존재하지 않을 떄
        /*if (request.getCategoryId() != null &&
                !todoCategoryRepository.existsById(request.getCategoryId())) {
            throw new CustomException(ErrorCode.TODO_CATEGORY_NOT_FOUND);
        }*/


        Todo todo = Todo.builder()
                .userId(userId)
                .title(request.getTitle())
                // dto는 LocalDate, 엔티티는 LocalDateTime(자정 기준)으로 저장
                .date(request.getDate().atStartOfDay())
                .categoryId(request.getCategoryId())
                .build();

        Todo saved = todoRepository.save(todo);

        return TodoCreateResponseDTO.from(saved);
    }
}
