package com.yeongho.book.springboot.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_PASSWORD(403, "비밀번호가 틀렸습니다."),
    FILE_DOWNLOAD_ERROR(500, "파일 처리가 정상적으로 이루어지지 않았습니다. 시스템 관리자에게 문의해주세요."),
    POST_NOT_FOUND(404, "조회할 수 없는 게시글입니다.");

    private final int statusCode;
    private final String message;

    ErrorCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }
}
