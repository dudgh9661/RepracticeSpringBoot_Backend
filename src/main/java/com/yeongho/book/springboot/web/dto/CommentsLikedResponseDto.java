package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.LikedComments;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CommentsLikedResponseDto {
    private Long id;

    public CommentsLikedResponseDto(LikedComments likedComments) {
        this.id = likedComments.getComment().getId();
    }

}
