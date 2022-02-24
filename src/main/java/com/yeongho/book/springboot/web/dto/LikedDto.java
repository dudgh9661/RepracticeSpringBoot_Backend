package com.yeongho.book.springboot.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
public class LikedDto {
    private int liked;

    public LikedDto(int liked) {
        this.liked = liked;
    }
}
