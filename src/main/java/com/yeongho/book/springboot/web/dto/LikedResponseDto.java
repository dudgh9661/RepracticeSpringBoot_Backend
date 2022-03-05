package com.yeongho.book.springboot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
@NoArgsConstructor
public class LikedResponseDto {
    private Boolean isLikedPost;
    private List<CommentsLikedResponseDto> likedCommentList;

    @Builder
    public LikedResponseDto(boolean isLikedPost, List<CommentsLikedResponseDto> likedCommentList) {
        this.isLikedPost = isLikedPost;
        this.likedCommentList = likedCommentList;
    }
}
