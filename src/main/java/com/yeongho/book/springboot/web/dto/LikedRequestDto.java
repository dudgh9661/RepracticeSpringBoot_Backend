package com.yeongho.book.springboot.web.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LikedRequestDto {
    private String ip;
    private Long postId;
    public LikedRequestDto(String ip) {
        this.ip = ip;
    }

    public LikedRequestDto(String ip, Long postId) {
        this.ip = ip;
        this.postId = postId;
    }
}
