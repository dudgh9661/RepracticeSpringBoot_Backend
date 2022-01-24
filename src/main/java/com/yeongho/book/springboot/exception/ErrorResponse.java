package com.yeongho.book.springboot.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;

    @Builder
    ErrorResponse (int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
