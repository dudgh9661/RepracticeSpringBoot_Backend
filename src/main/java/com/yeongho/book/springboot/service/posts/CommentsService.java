package com.yeongho.book.springboot.service.posts;

import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.CommentsRepository;
import com.yeongho.book.springboot.domain.posts.Posts;
import com.yeongho.book.springboot.domain.posts.PostsRepository;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import com.yeongho.book.springboot.web.dto.CommentsDeleteDto;
import com.yeongho.book.springboot.web.dto.CommentsSaveRequestDto;
import com.yeongho.book.springboot.web.dto.CommentsResponseDto;
import com.yeongho.book.springboot.web.dto.CommentsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    public List<CommentsResponseDto> findAll(Long postId) {
        Posts post = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다 id = " + postId));
        return commentsRepository.findAllByPost(post).stream()
                .map(CommentsResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentsResponseDto save(CommentsSaveRequestDto commentsSaveRequestDto) {
        Long postId = Long.parseLong(commentsSaveRequestDto.getPostId());
        Posts post = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + postId));
        Comments comment = commentsSaveRequestDto.toEntity(post);
        commentsRepository.save(comment);
        return new CommentsResponseDto(comment);
    }

    @Transactional
    public Long update(Long commentId, CommentsUpdateRequestDto commentsUpdateRequestDto) throws InvalidPasswordException {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        comments.update(commentsUpdateRequestDto);
        return commentId;
    }

    @Transactional
    public void delete(Long commentId, CommentsDeleteDto commentsDeleteDto) throws InvalidPasswordException {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        comments.delete(commentsDeleteDto);
    }

    public int getLiked(Long commentId) {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        return comments.getLiked();
    }

    @Transactional
    public int addLiked(Long commentId) {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        return comments.addLike();
    }

    @Transactional
    public int deleteLiked(Long commentId) {
        Comments comments = commentsRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));
        return comments.deleteLike();
    }
}
