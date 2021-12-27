package com.yeongho.book.springboot.service.posts;

import com.yeongho.book.springboot.domain.posts.Posts;
import com.yeongho.book.springboot.domain.posts.PostsRepository;
import com.yeongho.book.springboot.utils.FileUtils;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostsRepository postsRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileUtils fileUtils;

    @Transactional
    public Long save(PostsSaveRequestDto postsSaveRequestDto, MultipartFile file) throws IOException {
        // 비밀번호를 암호화해서 저장한다.
        postsSaveRequestDto.makePasswordEncoding(passwordEncoder.encode(postsSaveRequestDto.getPassword()));
        Posts posts = postsSaveRequestDto.toEntity();

        // 파일명이 존재하는 경우, 파일 엔티티를 생성한다.
        // 해당 파일과 연결된 게시물과 함께 데이터베이스에 저장한다.
        // 게시물을 저장하여 리턴한다.
        fileUtils.store(file,posts);
        return postsRepository.save(posts).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto, MultipartFile file) throws IOException {
        Posts posts = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));
        fileUtils.update(file, posts);
        posts.update(requestDto.getAuthor(), requestDto.getPassword(), requestDto.getTitle(), requestDto.getContent());
        return id;
    }

    public PostsResponseDto findById (Long id) {
        Posts entity = postsRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        return new PostsResponseDto(entity); //entity 객체를 넘겨, PostsResponseDto class에서 이를 가공해 response에 필요한 값들만 뽑아 쓸 것이다.
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAll () {
        return postsRepository.findAll().stream()
                .map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));
        postsRepository.delete(posts);
    }

}
