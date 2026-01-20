package com.req2res.actionarybe.domain.todo.service;


import com.req2res.actionarybe.domain.notification.service.NotificationService;
import com.req2res.actionarybe.domain.point.dto.TodoCompletionPointRequestDTO;
import com.req2res.actionarybe.domain.point.service.PointService;
import com.req2res.actionarybe.domain.todo.dto.*;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import com.req2res.actionarybe.domain.todo.repository.TodoCategoryRepository;
import com.req2res.actionarybe.domain.todo.repository.TodoRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final PointService pointService;



    //1. 투두 생성 API
    public TodoCreateResponseDTO createTodo(Long userId, TodoCreateRequestDTO request) {

        //title 항목 누락
        if (request.getTitle().isBlank()) {
            throw new CustomException(ErrorCode.TODO_INVALID_TITLE);
        }

        //date 항목 누락
        if (request.getDate() == null) {
            throw new CustomException(ErrorCode.TODO_INVALID_DATE);
        }

        // categoryId가 들어온 경우에만 검증
        if (request.getCategoryId() != null) {

            // 해당 categoryId가 "내 카테고리"로 존재하지 않으면 404
            if (!todoCategoryRepository.existsByIdAndUserId(request.getCategoryId(), userId)) {
                throw new CustomException(ErrorCode.TODO_CATEGORY_NOT_FOUND);
            }
        }

        Todo todo = Todo.builder()
                .userId(userId)
                .title(request.getTitle())
                .date(request.getDate())
                .categoryId(request.getCategoryId())
                .build();

        Todo saved = todoRepository.save(todo);

        return TodoCreateResponseDTO.from(saved);
    }

    //2. 특정 날짜 투두 목록 조회 API
    //특정 날짜(필수) + 카테고리(선택)로 투두 목록 조회
    @Transactional(readOnly = true)
    public DailyTodosResponseDTO getTodosByDate(Long userId, LocalDate date, Long categoryId) {

        if (date == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "date 값은 필수입니다.");
        }

        List<Todo> todos;

        if (categoryId != null) {
            // userId + date + categoryId 기준
            todos = todoRepository.findAllByUserIdAndDateAndCategoryId(userId, date, categoryId);
        } else {
            // userId + date 기준
            todos = todoRepository.findAllByUserIdAndDate(userId, date);
        }

        List<TodoResponseDTO> todoDtos = todos.stream()
                .map(TodoResponseDTO::from)
                .toList();

        return DailyTodosResponseDTO.builder()
                .date(date.toString())
                .todos(todoDtos)
                .build();
    }

    // 3. 투두 수정 API
    public TodoResponseDTO updateTodo(Long userId, Long todoId, TodoUpdateRequestDTO request) {

        // body가 완전히 비어 있는 경우 → 400 Bad Request
        if ((request.getTitle() == null || request.getTitle().isBlank())
                && request.getCategoryId() == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "수정할 내용이 없습니다.");
        }

        // 투두 존재 여부 확인 → 404 Not Found
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() ->
                        new CustomException(ErrorCode.NOT_FOUND, "해당 투두가 존재하지 않습니다."));

        // 제목 수정 (값이 들어왔을 때만)
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            todo.setTitle(request.getTitle());
        }

        // 카테고리 수정 (null이면 그대로 둠)
        if (request.getCategoryId() != null) {
            todo.setCategoryId(request.getCategoryId());
        }

        return TodoResponseDTO.from(todo);
    }

    //4. 투두 달성/실패 처리 API
    public TodoStatusResponseDTO updateTodoStatus(
            Long userId,
            Long todoId,
            TodoStatusUpdateRequestDTO request
    ) {
        String statusStr = request.getStatus();

        if (statusStr == null || statusStr.isBlank()) {
            throw new CustomException(ErrorCode.TODO_INVALID_STATUS);
        }

        Todo.Status newStatus;
        try {
            newStatus = Todo.Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.TODO_INVALID_STATUS);
        }

        if (newStatus == Todo.Status.PENDING) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "PENDING으로 변경할 수 없습니다.");
        }

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        if (!todo.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "본인의 투두만 상태를 변경할 수 있습니다.");
        }

        if (todo.getStatus() == newStatus) {
            throw new CustomException(ErrorCode.TODO_STATUS_CONFLICT);
        }

        // 1) 투두 상태 변경
        todo.setStatus(newStatus);

        // 2) DONE으로 바뀌는 순간에만 포인트 적립 시도
        // (FAILED로 바뀌는 건 포인트 적립 X)
        if (newStatus == Todo.Status.DONE) {
            try {
                TodoCompletionPointRequestDTO pointReq =
                        new TodoCompletionPointRequestDTO(todo.getId());

                // 같은 트랜잭션에서 수행됨
                pointService.earnTodoCompletionPoint(userId, pointReq);

            } catch (CustomException e) {

                // 정상 스킵 케이스는 "투두 DONE은 유지"
                // 1) 이미 이 to-do로 포인트 받음 (409)
                // 2) 오늘 한도 초과 -> PointService는 0P로 정상 응답해서 여기로 안 옴
                if (e.getErrorCode() == ErrorCode.TODO_POINT_ALREADY_EARNED) {
                    log.info("[To-do] Point already earned for todoId={}", todo.getId());
                } else {
                    //그 외는 진짜 실패 → 롤백시키는 게 안전
                    throw e;
                }
            }
        }

        return TodoStatusResponseDTO.from(todo);
    }


    //5. 투두 삭제 API
    //Hard delete 기법 사용
    public void deleteTodo(Long userId, Long todoId) {

        // 투두 없음 -> 404
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        // 소유자 검증 -> 403
        if (!todo.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN, "본인의 투두만 삭제할 수 있습니다.");
        }

        // 3) 실제 삭제 (하드 삭제)
        todoRepository.delete(todo);
    }
}
