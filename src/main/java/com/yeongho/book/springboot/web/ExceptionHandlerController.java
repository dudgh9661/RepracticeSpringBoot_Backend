package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.exception.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@Log4j2
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> CustomException(CustomException e) {
        log.error("Custom 예외 발생 " + e);
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(errorCode.getStatusCode())
                .message(errorCode.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorCode.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> exception(Exception e) {
        log.error("예외 발생 " + e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .statusCode(500)
                .message("문제가 발생했습니다. 시스템 관리자에게 문의해주세요.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(errorResponse.getStatusCode()));
    }
}
