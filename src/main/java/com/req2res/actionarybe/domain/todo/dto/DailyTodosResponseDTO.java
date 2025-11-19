package com.req2res.actionarybe.domain.todo.dto;
//'특정 날짜 투두 목록 조회 API'에서 사용하는 날짜별 투두 목록 DTO

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyTodosResponseDTO {

    private String date;                      // "2025-10-31"
    private List<TodoResponseDTO> todos;      // 투두 리스트 배열
}
