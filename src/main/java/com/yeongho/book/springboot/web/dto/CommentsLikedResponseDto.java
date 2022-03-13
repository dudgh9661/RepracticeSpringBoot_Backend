package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.LikeComment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CommentsLikedResponseDto {
    private Long id;

    public CommentsLikedResponseDto(LikeComment likeComment) {
        this.id = likeComment.getComment().getId();
    }

}
