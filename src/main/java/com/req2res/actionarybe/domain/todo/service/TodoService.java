package com.req2res.actionarybe.domain.todo.service;


import com.req2res.actionarybe.domain.notification.service.NotificationService;
import com.req2res.actionarybe.domain.todo.dto.*;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import com.req2res.actionarybe.domain.todo.repository.TodoRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final TodoRepository todoRepository;
    //private final NotificationService notificationService; -> TODO: 나중에 투두 달성/실패 처리 API에서 알림 생성 메서드 주입 예정


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

        /*
        TODO: 이미 삭제된 투두라면 409 Conflict로 예외 처리
        // 삭제 플래그(isDeleted) 등이 생기면 여기서 검사하면 됨.
        */

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
    public TodoStatusResponseDTO updateTodoStatus(Long userId, Long todoId,
                                                  TodoStatusUpdateRequestDTO request) {


        String statusStr = request.getStatus();

        // 1) status 값 검증 (DONE / FAILED 만 허용)
        if (statusStr == null || statusStr.isBlank()) {
            throw new CustomException(ErrorCode.TODO_INVALID_STATUS);
        }

        Todo.Status newStatus;
        try {
            newStatus = Todo.Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.TODO_INVALID_STATUS);
        }

        //status=PENDING으로 설정했을 때의 예외처리
        if (newStatus == Todo.Status.PENDING) {
            throw new CustomException(ErrorCode.BAD_REQUEST,
                    "PENDING으로 변경할 수 없습니다.");
        }

        // 2) 투두 조회 (없으면 404)
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        // 3) 사용자 검증
        if (!todo.getUserId().equals(userId)) {
             throw new CustomException(ErrorCode.FORBIDDEN, "본인의 투두만 상태를 변경할 수 있습니다.");
        }

        // 4) 이미 같은 상태라면 409 Conflict
        if (todo.getStatus() == newStatus) {
            throw new CustomException(ErrorCode.TODO_STATUS_CONFLICT);
        }

        // 5) 상태 변경
        todo.setStatus(newStatus);

        // 6) DONE으로 바뀐 경우: 하루 투두 모두 완료했는지 확인
        if (newStatus == Todo.Status.DONE) {
            checkAllTodosDoneAndNotify(todo);
        }

        return TodoStatusResponseDTO.from(todo);
    }

    /**
     * 하루 동안의 투두가 모두 DONE인지 확인하고,
     * 모두 달성 시 알림 생성 메서드를 호출할 자리.
     */
    private void checkAllTodosDoneAndNotify(Todo updatedTodo) {

        Long userId = updatedTodo.getUserId();
        LocalDate targetDate = updatedTodo.getDate();

        // 날짜 그대로 사용 (LocalDate로 저장되어 있으니까!)
        List<Todo> todosOfDay =
                todoRepository.findAllByUserIdAndDate(userId, targetDate);

        boolean allDone = todosOfDay.stream()
                .allMatch(todo -> todo.getStatus() == Todo.Status.DONE);

        if (allDone) {
            // TODO: 알림 생성 메서드 호출
            // notificationService.createAllTodosDoneNotification(userId, targetDate);
        }
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
