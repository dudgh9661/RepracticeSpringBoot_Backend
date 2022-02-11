package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    List<Posts> findByTitleContaining(String keyword);

    List<Posts> findByContentContaining(String keyword);

    List<Posts> findByAuthorContaining(String keyword);
}
