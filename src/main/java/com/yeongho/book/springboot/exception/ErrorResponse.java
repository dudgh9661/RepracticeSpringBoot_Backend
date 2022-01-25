package com.yeongho.book.springboot.exception;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class ErrorResponse {
    private int statusCode;
    private String message;

    @Builder
    public ErrorResponse (int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
