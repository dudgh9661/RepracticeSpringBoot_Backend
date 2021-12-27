package com.yeongho.book.springboot.service.posts;

import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.CommentsRepository;
import com.yeongho.book.springboot.domain.posts.Posts;
import com.yeongho.book.springboot.domain.posts.PostsRepository;
import com.yeongho.book.springboot.web.dto.CommentsSaveRequestDto;
import com.yeongho.book.springboot.web.dto.CommentsResponseDto;
import com.yeongho.book.springboot.web.dto.CommentsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentsService {

    private final PostsRepository postsRepository;
    private final CommentsRepository commentsRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<CommentsResponseDto> findAll() {
        return commentsRepository.findAll().stream()
                .map(CommentsResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long save(CommentsSaveRequestDto commentsSaveRequestDto) {
        // 1. 댓글을 다는 게시물의 Posts 객체를 불러온다.
        Long postId = commentsSaveRequestDto.getPostId();
        Posts post = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));
        // 2. 불러온 post 객체를 FK로 이용해 Comment data를 저장한다.
        commentsSaveRequestDto.setPasswordEncoding(passwordEncoder.encode(commentsSaveRequestDto.getPassword())); // pw 암호화
        return commentsRepository.save(commentsSaveRequestDto.toEntity(post)).getId();
    }

    @Transactional
    public Long update(Long id, CommentsUpdateRequestDto requestDto) {
        Comments comments = commentsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        comments.update(requestDto);
        return id;
    }

    @Transactional
    public void delete(Long id) {
        Comments comments = commentsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        commentsRepository.delete(comments);
    }
}
