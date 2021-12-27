package com.yeongho.book.springboot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentsUpdateRequestDto {
    private String password;
    private String comment;

    @Builder
    public CommentsUpdateRequestDto(String password, String comment) {
        this.password = password;
        this.comment = comment;
    }
}
