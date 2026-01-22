package com.req2res.actionarybe.domain.todo.controller;

import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.domain.todo.dto.TodoCalendarDoneSummaryDTO;
import com.req2res.actionarybe.domain.todo.service.TodoCalendarService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/todos/calendar")
public class TodoCalendarController {

    private final TodoCalendarService todoCalendarService;
    private final MemberRepository memberRepository;

    @Operation(
            summary = "캘린더 월별 DONE 투두 집계 조회",
            description = """
                    캘린더 화면에서 사용하기 위한 API입니다.
                    <br/>
                    선택한 연/월에 해당하는 기간 동안,
                    <b>DONE 상태인 투두만</b> 날짜별로 집계하여 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "캘린더 월별 DONE 투두 집계 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (year/month 값 오류)"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요)")
    })
    @GetMapping("/summary/monthly")
    public Response<List<TodoCalendarDoneSummaryDTO>> getMonthlyDoneSummary(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "조회할 연도", example = "2026", required = true)
            @RequestParam int year,

            @Parameter(description = "조회할 월 (1~12)", example = "1", required = true)
            @RequestParam int month
    ) {
        if (userDetails == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String loginId = userDetails.getUsername();

        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        Long userId = member.getId();

        List<TodoCalendarDoneSummaryDTO> data =
                todoCalendarService.getMonthlyDoneSummary(userId, year, month);

        return Response.success("캘린더 월별 DONE 투두 집계 조회에 성공했습니다.", data);
    }
}

