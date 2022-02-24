package com.yeongho.book.springboot.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikedDto {
    private int Liked;

    public LikedDto(int Liked) {
        this.Liked = Liked;
    }
}
