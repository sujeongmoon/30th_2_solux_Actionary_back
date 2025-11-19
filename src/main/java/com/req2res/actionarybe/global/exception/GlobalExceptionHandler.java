package com.req2res.actionarybe.global.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.req2res.actionarybe.global.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<Response<?>> handleCustomException(CustomException ex) {
        ex.printStackTrace();
        ErrorCode errorCode = ex.getErrorCode();
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
        ex.printStackTrace();
        ErrorCode errorCode = ErrorCode.BAD_REQUEST;

        return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Response<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getMostSpecificCause();
        ErrorCode errorCode; // 초기화

        if (cause instanceof JsonParseException) {
            errorCode=ErrorCode.JSON_SYNTAX_ERROR;
        } else if (cause instanceof MismatchedInputException) {
            errorCode=ErrorCode.INVALID_FIELD_TYPE;
        } else{
            errorCode=ErrorCode.JSON_SYNTAX_ERROR;
        }

        return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<Response<?>> handleBadCredentials(BadCredentialsException ex) {
        ex.printStackTrace();
        ErrorCode errorCode=ErrorCode.BAD_CREDENTIALS;

        return new ResponseEntity<>(Response.fail(errorCode.getMessage()),errorCode.getStatus());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Response<?>> handleConstraintViolation(ConstraintViolationException ex) {
        ex.printStackTrace();
        ErrorCode errorCode=ErrorCode.INVALID_CONSTRAINT;

        return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<Response<?>> handleMissingToken(MissingRequestHeaderException ex) {
        ex.printStackTrace();
        ErrorCode errorCode = ErrorCode.MISSING_TOKEN;
        return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    protected ResponseEntity<Response<?>> handleExpiredToken(ExpiredJwtException ex) {
        ex.printStackTrace();
        ErrorCode errorCode = ErrorCode.EXPIRED_TOKEN;
        return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
    }

    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<Response<?>> handleInvalidToken(JwtException ex) {
        ex.printStackTrace();
        ErrorCode errorCode = ErrorCode.INVALID_TOKEN;
        return new ResponseEntity<>(Response.fail(errorCode.getMessage()), errorCode.getStatus());
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Response<?>> handleException(Exception ex) {
        ex.printStackTrace();
        ErrorCode errorCode=ErrorCode.INTERNAL_SERVER_ERROR;

        return new ResponseEntity<>(Response.fail(errorCode.getMessage()),errorCode.getStatus());
    }
}
