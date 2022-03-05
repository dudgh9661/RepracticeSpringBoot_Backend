package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikePostsRepository extends JpaRepository<LikedPosts, Long> {
    Optional<LikedPosts> findByPostAndIp(Posts post, String ip);

}
