package com.req2res.actionarybe.domain.todo.service;


import com.req2res.actionarybe.domain.todo.dto.DailyTodosResponseDTO;
import com.req2res.actionarybe.domain.todo.dto.TodoCreateRequestDTO;
import com.req2res.actionarybe.domain.todo.dto.TodoCreateResponseDTO;
import com.req2res.actionarybe.domain.todo.dto.TodoResponseDTO;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import com.req2res.actionarybe.domain.todo.repository.TodoRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
                .date(request.getDate())
                .categoryId(request.getCategoryId())
                .build();

        Todo saved = todoRepository.save(todo);

        return TodoCreateResponseDTO.from(saved);
    }

    //특정 날짜 투두 목록 조회 API
    //특정 날짜(필수) + 카테고리(선택)로 투두 목록 조회
    public DailyTodosResponseDTO getTodosByDate(LocalDate date, Long categoryId) {

        // date가 null로 들어올 일은 거의 없지만 / 방어 코드
        if (date == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "date 값은 필수입니다.");
        }

        // 카테고리 여부에 따른 분기
        List<Todo> todos;
        //categoryId 들어왔을 때
        if (categoryId != null) {
            todos = todoRepository.findAllByDateAndCategoryId(date, categoryId);
        }
        // categoryId 안들어왔을 때
        else {
            todos = todoRepository.findAllByDate(date);
        }

        // 해당 날짜에 투두가 하나도 없을 때 404
        if (todos.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_FOUND, "해당 날짜에 투두가 아예 없습니다.");
        }

        List<TodoResponseDTO> todoDtos = todos.stream()
                .map(TodoResponseDTO::from)
                .toList();

        return DailyTodosResponseDTO.builder()
                .date(date.toString())
                .todos(todoDtos)
                .build();
    }
}
