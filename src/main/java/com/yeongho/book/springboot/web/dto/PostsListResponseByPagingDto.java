package com.yeongho.book.springboot.web.dto;

import com.yeongho.book.springboot.domain.posts.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class PostsListResponseByPagingDto {
    private List<PostsListResponseDto> postsList;
    private int currentPageNumber;
    private int totalPage;
    private Long totalElement;

    public PostsListResponseByPagingDto(Page<Post> postsPage) {
        this.postsList = postsPage.getContent()
                .stream().map(PostsListResponseDto::new).collect(Collectors.toList());
        this.currentPageNumber = postsPage.getPageable().getPageNumber();
        this.totalPage = postsPage.getTotalPages();
        this.totalElement = postsPage.getTotalElements();
    }
}
