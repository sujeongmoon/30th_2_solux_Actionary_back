package com.req2res.actionarybe.domain.todo.service;

import com.req2res.actionarybe.domain.todo.dto.TodoCalendarDoneSummaryDTO;
import com.req2res.actionarybe.domain.todo.entity.Todo;
import com.req2res.actionarybe.domain.todo.repository.TodoDoneCountByDate;
import com.req2res.actionarybe.domain.todo.repository.TodoRepository;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoCalendarService {

    private final TodoRepository todoRepository;

    public List<TodoCalendarDoneSummaryDTO> getMonthlyDoneSummary(
            Long userId,
            int year,
            int month
    ) {
        // month 유효성 검사
        if (month < 1 || month > 12) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 해당 월의 시작일 ~ 말일 계산
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // DONE 상태만 집계
        List<TodoDoneCountByDate> result =
                todoRepository.countDoneTodosByDateInMonth(
                        userId,
                        Todo.Status.DONE,
                        startDate,
                        endDate
                );

        // Projection → DTO 변환
        return result.stream()
                .map(r -> new TodoCalendarDoneSummaryDTO(
                        r.getDate(),
                        r.getDoneCount()
                ))
                .toList();
    }
}
