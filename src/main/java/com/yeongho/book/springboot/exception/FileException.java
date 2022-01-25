package com.yeongho.book.springboot.exception;

public class FileException extends CustomException {
    public FileException() {
        super(ErrorCode.FILE_DOWNLOAD_ERROR);
    }
}
