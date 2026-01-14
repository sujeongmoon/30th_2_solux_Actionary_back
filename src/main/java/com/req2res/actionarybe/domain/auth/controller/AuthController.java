package com.req2res.actionarybe.domain.auth.controller;

import com.req2res.actionarybe.domain.auth.service.AuthService;
import com.req2res.actionarybe.global.Response;
import com.req2res.actionarybe.domain.auth.dto.*;
import com.req2res.actionarybe.global.security.CustomUserDetails;
import com.req2res.actionarybe.global.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;

    // ===================== 회원가입 =====================
    @Operation(summary = "회원가입", description = "회원가입 기능")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원가입 완료",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "회원가입이 완료되었습니다.",
                      "data": {
                        "memberId": 1,
                        "loginId": "newUser123",
                        "nickname": "새로운유저"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 입력값이 빠지거나 형식이 틀린 경우",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "입력값이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "중복됨 - 이미 존재하는 사용자",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "이미 존재하는 회원입니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 서버 내부 문제",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PostMapping(
            value = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Response<SignupResponseDTO>> signup(
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "signupInfo")
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid SignupRequestDTO req
    ) {
        SignupResponseDTO result = authService.signup(req, profileImage);
        return ResponseEntity.ok(Response.success("회원가입에 성공하였습니다.", result));
    }

    // ===================== 로그인 (POST) =====================
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "로그인 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "로그인에 성공하였습니다.",
                      "data": {
                        "memberId": 1,
                        "nickname": "유저1234",
                        "profileImageUrl": "http://.../default_profile.png",
                        "accessToken": "eyJhbGciOiJIU...[생성된 JWT]...a5Y",
                        "refreshToken": "eyJhbGciOiJIU...[생성된 REFRESH JWT]...x8F"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 입력값이 빠지거나 형식이 틀린 경우",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "아이디 또는 비밀번호 형식이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요 - 토큰이 없거나 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "인증 정보가 유효하지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 서버측 문제",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponseDTO>> login(@Valid @RequestBody LoginRequestDTO req) {
        LoginResponseDTO result = authService.login(req);
        return ResponseEntity.ok(Response.success("로그인에 성공하였습니다.", result));
    }

    // ===================== 회원 탈퇴 (DELETE) =====================
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 회원을 탈퇴 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "회원 탈퇴 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "회원 탈퇴가 성공적으로 처리되었습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 삭제할 ID가 잘못된 경우",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 정보가 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요 - Authorization 헤더가 없거나 토큰이 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "로그인이 필요합니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "삭제 금지 - 삭제 권한이 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "삭제 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "찾을 수 없음 - 이미 삭제되었거나 존재하지 않는 회원",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "해당 회원을 찾을 수 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 삭제 중 서버 내부 오류",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @DeleteMapping("/withdraw")
    public ResponseEntity<Response<Void>> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails){
        Long id = userDetails.getId();
        authService.withdrawMember(id);
        return ResponseEntity.ok(Response.success("회원 탈퇴에 성공하였습니다.",null));
    }

    // ===================== 로그인 유지 / 토큰 재발급 (POST) =====================
    @Operation(summary = "Access Token 재발급", description = "Refresh Token을 이용하여 Access Token을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Access Token 재발급 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "message": "AccessToken 재발급 완료",
                      "data": {
                        "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyMTIzNCIsImlhdCI6MTc2Njg1MzIwNiwiZXhwIjoxNzY2ODU2ODA2fQ.Ud6cfSMOZbTyT7Cg-OZq8VlcKCzPDzz8jTzs2UUxrBQ"
                      }
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 - 입력값이 빠지거나 형식이 틀린 경우",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "요청 값이 올바르지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "로그인 필요 - Authorization 헤더가 없거나 토큰이 유효하지 않음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "Refresh Token이 유효하지 않습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "권한 없음 - 토큰 재발급 권한이 없음",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "토큰 재발급 권한이 없습니다."
                    }
                    """))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 오류 - 서버 내부 문제",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
                    {
                      "success": false,
                      "message": "서버 오류가 발생했습니다."
                    }
                    """))
            )
    })
    @PostMapping("/refresh")
    public ResponseEntity<Response<RefreshTokenResponseDTO>> refreshAccessToken(
            @RequestBody @Valid RefreshTokenRequestDTO request
    ) {
        RefreshTokenResponseDTO result = authService.refreshToken(request);
        return ResponseEntity.ok(Response.success("accessToken 발급 성공",result));
    }
}