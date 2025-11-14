package com.req2res.actionarybe.global.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.req2res.actionarybe.global.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomException.class)
	protected ResponseEntity<Response<?>> handleDuplicateException(CustomException ex) {
		ErrorCode errorCode = ex.getErrorCode();

		ex.printStackTrace();
		return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected ResponseEntity<Response<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		String message = ex.getBindingResult().getFieldErrors().stream()
			.map(FieldError::getDefaultMessage)
			.findFirst()
			.orElse("요청값이 올바르지 않습니다.");

		return new ResponseEntity<>(Response.fail(message), HttpStatus.BAD_REQUEST);
	}

    @ExceptionHandler(UnrecognizedPropertyException.class)
    protected ResponseEntity<Response<?>> handleUnknownProperty(UnrecognizedPropertyException ex) {
        String message = "알 수 없는 필드: " + ex.getPropertyName();
        return new ResponseEntity<>(Response.fail(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Response<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        String message = "요청 JSON 형식이 올바르지 않습니다.";

        if (cause instanceof JsonParseException) {
            message = "JSON 문법이 잘못되었습니다. (콤마, 중괄호, 따옴표 등을 확인하세요)";
        } else if (cause instanceof MismatchedInputException) {
            message = "JSON 필드 타입이 올바르지 않습니다.";
        }

        return new ResponseEntity<>(Response.fail(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Response<?>> handleBadCredentials(BadCredentialsException ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(Response.fail("아이디 또는 비밀번호가 올바르지 않습니다."),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Response<?>> handleConstraintViolation(ConstraintViolationException ex) {
        return new ResponseEntity<>(Response.fail("요청 값이 올바르지 않습니다."), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Response<?>> handleException(Exception ex) {
        ex.printStackTrace();
        return new ResponseEntity<>(Response.fail("서버 내부 오류가 발생했습니다."),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
