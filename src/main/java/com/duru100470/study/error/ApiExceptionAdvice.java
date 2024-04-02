package com.duru100470.study.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ApiExceptionAdvice {
    @ExceptionHandler({ApiException.class})
    public ResponseEntity<?> exceptionHandler(HttpServletRequest request, final ApiException e) {
        final ErrorResponse errorResponse = 
            ErrorResponse.builder(e, e.getError().getStatus(), e.getMessage())
                .build();

        return ResponseEntity.status(e.getError().getStatus()).body(errorResponse);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<?> validException(MethodArgumentNotValidException e) {
        final ErrorResponse errorResponse = 
            ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, "유효성 검사 실패 : " + e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
