package com.yeongho.book.springboot.domain.posts;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByTitleContaining(Pageable pageable, String keyword);
    List<Post> findByTitleContaining(String keyword);

    Page<Post> findByContentContaining(Pageable pageable, String keyword);
    List<Post> findByContentContaining(String keyword);

    Page<Post> findByAuthorContaining(Pageable pageable, String keyword);
    List<Post> findByAuthorContaining(String keyword);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select p from Post p where p.id = :id")
    Optional<Post> findByIdForUpdate(@Param("id") Long id);
}
