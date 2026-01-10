package com.req2res.actionarybe.domain.aisummary.controller;

import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryResponseDataDTO;
import com.req2res.actionarybe.domain.aisummary.dto.AiSummaryUrlRequestDTO;
import com.req2res.actionarybe.domain.aisummary.service.AiSummaryService;
import com.req2res.actionarybe.domain.member.entity.Member;
import com.req2res.actionarybe.domain.member.repository.MemberRepository;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.exception.CustomException;
import com.req2res.actionarybe.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
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
    private final MemberRepository memberRepository;

    // A) 파일 업로드 요약
    @Operation(
            summary = "파일 요약",
            description = "PDF/문서 파일을 업로드하여 AI 요약을 수행합니다. 비동기 처리 시 202 상태로 jobId를 반환합니다."
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

        if (data.getStatus() == AiSummaryResponseDataDTO.Status.PENDING) {
            return ResponseEntity
                    .accepted()
                    .body(Response.success("요약 작업이 접수되었습니다.", data));
        }

        if (data.getStatus() == AiSummaryResponseDataDTO.Status.FAILED) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.fail("요약 처리 중 오류가 발생했습니다."));
        }

        // SUCCEEDED
        return ResponseEntity.ok(
                Response.success("요약이 완료되었습니다.", data)
        );
    }

    // B) URL 요약
    @Operation(
            summary = "URL 요약",
            description = "URL로 제공된 문서를 AI로 요약합니다. (application/json)"
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

        if (data.getStatus() == AiSummaryResponseDataDTO.Status.PENDING) {
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
}
