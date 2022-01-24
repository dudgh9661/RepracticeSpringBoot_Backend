package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.exception.ErrorCode;
import com.yeongho.book.springboot.exception.ErrorResponse;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(InvalidPasswordException.class)
    protected ResponseEntity<ErrorResponse> invalidPasswordException(InvalidPasswordException e) {
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(errorCode.getStatusCode())
                .message(errorCode.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> Exception(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(500)
                .message("문제가 발생했습니다. 시스템 관리자에게 문의해주세요.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatusCode()));
    }
}
