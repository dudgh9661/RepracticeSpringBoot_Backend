package com.yeongho.book.springboot.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_PASSWORD(403, "비밀번호가 틀렸습니다.");

    private final int statusCode;
    private final String message;

    ErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
