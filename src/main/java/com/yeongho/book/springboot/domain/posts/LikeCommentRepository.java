package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LikeCommentRepository extends JpaRepository<LikeComment, Long> {
    Optional<LikeComment> findByCommentAndIp(Comment comment, String ip);
    Optional<List<LikeComment>> findAllByCommentAndIp(Comment comment, String ip);
    Optional<List<LikeComment>> findAllByPostAndIp(Post post, String ip);
}
