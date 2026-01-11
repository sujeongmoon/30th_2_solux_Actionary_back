package com.req2res.actionarybe.domain.member.controller;

import com.req2res.actionarybe.domain.member.dto.*;
import com.req2res.actionarybe.domain.member.service.MemberService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // ===================== 로그인 유저 정보 조회 (GET) =====================
    @Operation(summary = "로그인 유저 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @SecurityRequirement(name = "Bearer Token")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "사용자 정보 조회가 완료되었습니다.",
                      "data": {
                        "memberId": 1,
                        "profileImageUrl": "https://example.com/images/default.png",
                        "nickname": "솔룩스123",
                        "phoneNumber": "010-1234-5678",
                        "birthday": "1995-10-25"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 형식이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "접근 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 정보 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 회원을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @GetMapping("/me/info")
    public Response<LoginMemberResponseDTO> meInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long id = userDetails.getId();
        LoginMemberResponseDTO result = memberService.getLoginMemberInfo(id);
        return Response.success("로그인 유저 정보 조회 성공", result);
    }

    // ===================== 타인 정보 조회 (GET) =====================
    @Operation(summary = "타인 정보 조회", description = "회원 ID를 통해 다른 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "타인정보 조회 성공",
                      "data": {
                        "memberId": 2,
                        "nickname": "other nickname",
                        "profileImageUrl": "https://example.com/images/other.png"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 형식이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "접근 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 정보 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 회원을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @GetMapping("/{memberId}")
    public Response<OtherMemberResponseDTO> memberInfo(
            @PathVariable("memberId") Long memberId
    ){
        OtherMemberResponseDTO result=memberService.getOtherMemberInfo(memberId);
        return Response.success("타인정보 조회 성공",result);
    }

    // ===================== 프로필 사진 수정 (PATCH) =====================
    @Operation(summary = "프로필 사진 수정", description = "로그인한 사용자의 프로필 사진을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "프로필 변경 성공"
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 값이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "수정 권한 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "수정 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 정보 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 회원을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PatchMapping("me/profile")
    public Response<UpdateProfileRequestDTO> profile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateProfileRequestDTO updateProfileRequestDTO
    ) {
        Long id=userDetails.getId();
        memberService.updateProfile(id,updateProfileRequestDTO.getImageUrl());
        return Response.success("프로필 사진 변경에 성공했습니다.",null);
    }

    // ===================== 닉네임 수정 (PATCH) =====================
    @Operation(summary = "닉네임 수정", description = "로그인한 사용자의 닉네임을 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "수정 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "닉네임이 성공적으로 변경되었습니다.",
                      "data": {
                        "memberId": 123,
                        "nickname": "새로운닉네임"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 값이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "수정 권한 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "수정 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 정보 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 회원을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PatchMapping("me/nickname")
    public Response<UpdateNicknameResponseDTO> nickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody @Valid UpdateNicknameRequestDTO updateNickNameRequestDTO
    ){
        Long id = userDetails.getId();
        String nickname=updateNickNameRequestDTO.getNickname();
        UpdateNicknameResponseDTO result=memberService.updateNickname(id,nickname);
        return Response.success("닉네임 변경에 성공했습니다.",result);
    }

    // ===================== 뱃지 조회 (GET) =====================
    @Operation(summary = "뱃지 조회", description = "로그인한 사용자의 뱃지 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "뱃지 조회에 성공했습니다.",
                      "result": {
                        "badgeId": 2,
                        "badgeName": "10p",
                        "requiredPoint": 10,
                        "badgeImageUrl": "https://.../images/badge_level_1.png"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 형식이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "접근 권한 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "접근 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "뱃지 정보 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "뱃지 정보를 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @GetMapping("me/badge")
    public Response<BadgeResponseDTO> badge(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ){
        Long id=userDetails.getId();
        BadgeResponseDTO result = memberService.badge(id);
        return Response.success("뱃지 정보 조회에 성공했습니다.",result);
    }
}