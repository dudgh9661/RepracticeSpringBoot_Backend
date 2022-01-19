package com.yeongho.book.springboot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsUpdateRequestDto {
    private String password;
    private String text;

    @Builder
    public CommentsUpdateRequestDto(String password, String text) {
        this.password = password;
        this.text = text;
    }
}
