package com.yeongho.book.springboot.exception;

public class PostsException extends CustomException {
    public PostsException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
