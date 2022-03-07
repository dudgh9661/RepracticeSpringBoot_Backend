package com.yeongho.book.springboot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsUpdateRequestDto {
    private String text;

    @Builder
    public CommentsUpdateRequestDto(String text) {
        this.text = text;
    }
}
