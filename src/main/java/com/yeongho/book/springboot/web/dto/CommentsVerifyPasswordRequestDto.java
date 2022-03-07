package com.yeongho.book.springboot.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsVerifyPasswordRequestDto {
    private String password;

    public CommentsVerifyPasswordRequestDto (String password) {
        this.password = password;
    }
}
