package com.req2res.actionarybe.domain.todo.repository;

import com.req2res.actionarybe.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {

    // 특정 유저 + 날짜 + 카테고리로 투두 조회
    List<Todo> findAllByUserIdAndDateAndCategoryId(Long userId, LocalDate date, Long categoryId);

    // 특정 유저 + 특정 날짜의 투두 조회
    List<Todo> findAllByUserIdAndDate(Long userId, LocalDate date);

    // 특정 유저 + 특정 카테고리 ID로 조회
    boolean existsByUserIdAndCategoryId(Long userId, Long categoryId);

    // 특정 카테고리 ID + 특정 상태로 조회
    @Query("""
        select (count(t) > 0)
        from Todo t
        where t.userId = :userId
          and t.categoryId = :categoryId
          and t.status in :statuses
    """)
    boolean existsByCategoryIdAndStatuses(@Param("userId") Long userId,
                                          @Param("categoryId") Long categoryId,
                                          @Param("statuses") Collection<Todo.Status> statuses);

    /**
     * 특정 월 범위에서 날짜별로
     * - DONE 개수(doneCount)
     * - 전체 투두 개수(totalTodoCount)
     * 를 함께 집계한다.
     */
    @Query("""
        select
            t.date as date,
            sum(case when t.status = :doneStatus then 1 else 0 end) as doneCount,
            count(t) as totalTodoCount
        from Todo t
        where t.userId = :userId
          and t.date between :startDate and :endDate
        group by t.date
        order by t.date asc
    """)
    List<TodoDoneCountByDate> countTodoSummaryByDateInMonth(
            @Param("userId") Long userId,
            @Param("doneStatus") Todo.Status doneStatus,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
