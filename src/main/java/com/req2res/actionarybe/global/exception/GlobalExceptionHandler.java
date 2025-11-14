package com.req2res.actionarybe.global.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.req2res.actionarybe.global.common.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400: @Valid 바인딩 실패(필수 누락/형식 오류)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleValid(MethodArgumentNotValidException ex) {
        var first = ex.getBindingResult().getFieldErrors().stream().findFirst();
        String msg = first.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("잘못된 요청입니다.");
        return ResponseEntity.badRequest().body(ApiResponse.fail(msg));
    }

    // 400: DTO에 정의되지 않은 필드가 들어왔을 때 (unknown property)
    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(UnrecognizedPropertyException ex) {
        String msg = "알 수 없는 필드: " + ex.getPropertyName();
        return ResponseEntity.badRequest().body(ApiResponse.fail(msg));
    }

    // JSON 문법 / 구조가 잘못되었을 때 (콤마 빠짐, { } 짝 안맞음 등)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        Throwable cause = ex.getMostSpecificCause();
        String message = "요청 JSON 형식이 올바르지 않습니다.";

        // 1) JSON 문법 자체가 깨졌을 때 (예: 콤마 빠짐, 따옴표 안 닫음 등)
        if (cause instanceof JsonParseException) {
            message = "JSON 문법이 잘못되었습니다. (콤마, 중괄호, 따옴표 등을 확인하세요)";
        }
        // 2) 타입이 안 맞을 때 (예: password에 숫자 넣음)
        else if (cause instanceof MismatchedInputException) {
            message = "JSON 필드 타입이 올바르지 않습니다.";
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(message));
    }

    // 401: 아이디/비밀번호 불일치
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    // 그 외(원하면 추가): 파라미터 제약 위반
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraint(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(ApiResponse.fail("요청 값이 올바르지 않습니다."));
    }
}
