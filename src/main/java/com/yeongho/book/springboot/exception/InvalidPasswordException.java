package com.yeongho.book.springboot.exception;

import lombok.Getter;

@Getter
public class InvalidPasswordException extends CustomException{
    public InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD);
    }
}
