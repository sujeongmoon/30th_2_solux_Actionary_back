package com.req2res.actionarybe.domain.aisummary.controller;

import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryJobGetResponseDTO;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryListResponseDTO;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryUrlRequestDTO;
import com.req2res.actionarybe.domain.aisummary.entity.AiSummaryEnums;
import com.req2res.actionarybe.domain.aisummary.service.AiSummaryQueryService;
import com.req2res.actionarybe.domain.aisummary.service.AiSummaryService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai-summary")
@Tag(name = "AI Summary", description = "AI 기반 파일/URL 요약 API")
public class AiSummaryController {

    private final AiSummaryService aiSummaryService;
    private final AiSummaryQueryService queryService;
    private final MemberRepository memberRepository;

    // 1. 파일 업로드 요약
    @Operation(
            summary = "파일 요약",
            description = "PDF/문서 파일을 업로드하여 AI 요약을 수행합니다. 비동기 처리 시 202 상태로 jobId를 반환합니다.언어는 ko,en 중에 " +
                    "입력하면 되며, 토큰은 600-1000 입력하면 됩니다"
    )
    @PostMapping(value = "/file",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<AiSummaryResponseDataDTO>> summarizeFile(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "language", required = false) String language,
            @RequestParam(value = "maxTokens", required = false) Integer maxTokens,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = null;
        if (userDetails != null) {
            String loginId = userDetails.getUsername();
            Member member = memberRepository.findByLoginId(loginId)
                    .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
            userId = member.getId();
        }

        AiSummaryResponseDataDTO data =
                aiSummaryService.summarizeFile(file, language, maxTokens, userId);

        if (data.getStatus() == AiSummaryEnums.Status.PENDING) {
            return ResponseEntity
                    .accepted()
                    .body(Response.success("요약 작업이 접수되었습니다.", data));
        }

        if (data.getStatus() == AiSummaryEnums.Status.FAILED) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.fail("요약 처리 중 오류가 발생했습니다."));
        }

        // SUCCEEDED
        return ResponseEntity.ok(
                Response.success("요약이 완료되었습니다.", data)
        );
    }

    // 2. URL 요약
    @Operation(
            summary = "URL 요약",
            description = "URL로 제공된 문서를 AI로 요약합니다. 비동기 처리 시 202 상태로 jobId를 반환합니다."
    )
    @PostMapping(
            value = "/url",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Response<AiSummaryResponseDataDTO>> summarizeUrl(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AiSummaryUrlRequestDTO request
    ) {
        Long userId = extractUserId(userDetails);

        AiSummaryResponseDataDTO data = aiSummaryService.summarizeUrl(request, userId);

        if (data.getStatus() == AiSummaryEnums.Status.PENDING) {
            return ResponseEntity.accepted()
                    .body(Response.success("요약 작업이 접수되었습니다.", data));
        }

        return ResponseEntity.ok(Response.success("요약이 완료되었습니다.", data));
    }

    /**
     * 로그인 안 한 사용자면 null, 로그인 사용자면 memberId 반환
     */
    private Long extractUserId(UserDetails userDetails) {
        if (userDetails == null) return null;

        String loginId = userDetails.getUsername();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        return member.getId();
    }

    // 3.요약 작업 단건 조회 API (상태/결과 확인)
    @Operation(
            summary = "AI 요약 작업 단건 조회",
            description = """
                요약 작업의 현재 상태 및 결과를 조회합니다.
                
                - PENDING / RUNNING: 진행 상태 정보 반환
                - SUCCEEDED: 요약 결과 반환
                - FAILED: 오류 정보 반환
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요약 작업 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (jobId 형식 오류)"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 jobId"),
            @ApiResponse(responseCode = "429", description = "요청 과다 (폴링 과도)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/{jobId}")
    public ResponseEntity<Response<AiSummaryJobGetResponseDTO>> getSummaryJob(
            @PathVariable String jobId
    ) {
        AiSummaryJobGetResponseDTO data = queryService.getSummaryJob(jobId);

        return ResponseEntity.ok(
                Response.success(queryService.resolveMessage(data.getStatus()), data)
        );
    }

    // 4. 내 요약 목록 조회 API
    @Operation(
            summary = "내 요약 목록 조회",
            description = """
            로그인한 사용자가 요청한 모든 요약 이력(동기 + 비동기 job 포함)을 조회합니다.

            - Query Parameter를 받지 않습니다.
            - 기본값으로 고정:
              • page = 1
              • size = 10
              • sort = createdAt,DESC
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "요약 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음/만료)"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping
    public ResponseEntity<Response<AiSummaryListResponseDTO>> getMySummaryList(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 로그인 필수
        if (userDetails == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        String loginId = userDetails.getUsername();
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Long userId = member.getId();

        // 기본값 고정
        int page = 1;
        int size = 10;
        String sort = "createdAt,DESC";

        AiSummaryListResponseDTO data = queryService.getMySummaryList(userId);

        return ResponseEntity.ok(
                Response.success("요약 목록 조회에 성공했습니다.", data)
        );
    }

}
