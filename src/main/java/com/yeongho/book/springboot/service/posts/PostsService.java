package com.yeongho.book.springboot.service.posts;

import com.yeongho.book.springboot.domain.posts.*;
import com.yeongho.book.springboot.exception.FileException;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import com.yeongho.book.springboot.exception.PostsException;
import com.yeongho.book.springboot.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Service
public class PostsService {

    private final PostRepository postRepository;
    private final FileRepository fileRepository;
    private final LikePostRepository likePostRepository;
    private final LikeCommentRepository likeCommentRepository;

    @Transactional
    public Long save(PostsSaveRequestDto postsSaveRequestDto, List<MultipartFile> files) throws FileException, InvalidPasswordException {
        Post post = postsSaveRequestDto.toEntity();
        post.saveFile(files);
        return postRepository.save(post).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto, List<MultipartFile> files) throws FileException, InvalidPasswordException {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));
        post.update(requestDto.getAuthor(), requestDto.getPassword(), requestDto.getTitle(), requestDto.getContent(), files);

        return id;
    }

    @Transactional
    public PostsResponseDto findById (Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        log.info("Before 조회수 : " + post.getViewCount());
        post.addViewCount();
        log.info("After 조회수 : " + post.getViewCount());
        return new PostsResponseDto(post); //entity 객체를 넘겨, PostsResponseDto class에서 이를 가공해 response에 필요한 값들만 뽑아 쓸 것이다.
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAll () {
        return postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdTime")).stream()
                .map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostsListResponseByPagingDto findAllByPage(Pageable pageable) {
        Page<Post> postsPage = postRepository.findAll(pageable);
        return new PostsListResponseByPagingDto(postsPage);
    }

    @Transactional
    public void delete(Long id, String password) throws FileException, InvalidPasswordException {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id = " + id));
        post.verifyPassword(password);
        postRepository.delete(post);
    }

    public ResponseEntity<Resource> fileDownload(Long id) throws FileException {

        FileItem fileItem = fileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 파일이 없습니다. id = " + id));

        try {
            return fileItem.download();
        } catch (IOException e) {
            throw new FileException();
        }
    }

    public List<PostsListResponseDto> findByCondition(String searchType, String keyword) {
        List<Post> posts = new ArrayList<>();
        switch(searchType) {
            case "title" :
                posts = postRepository.findByTitleContaining(keyword);
                break;
            case "content" :
                posts = postRepository.findByContentContaining(keyword);
                break;
            case "author" :
                posts = postRepository.findByAuthorContaining(keyword);
                break;
        }

        List<PostsListResponseDto> result = posts.stream().map(PostsListResponseDto::new).collect(Collectors.toList());
        log.info(searchType + "_검색결과 => " + result.toString());
        return result;
    }

    public PostsListResponseByPagingDto findByConditionAndPage(Pageable pageable, String searchType, String keyword) {
        Page<Post> postsPage = null;
        switch(searchType) {
            case "title" :
                postsPage = postRepository.findByTitleContaining(pageable, keyword);
                break;
            case "content" :
                postsPage = postRepository.findByContentContaining(pageable, keyword);
                break;
            case "author" :
                postsPage = postRepository.findByAuthorContaining(pageable, keyword);
                break;
        }

        return new PostsListResponseByPagingDto(postsPage);
    }


    public int getLiked(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. postId = " + postId));
        return post.getLiked();
    }

    @Transactional
    public int addLiked(Long postId, String ip) {
        log.info("Post addLiked 시작 ::: postsId : " + postId + " ip : " + ip);
        Post post = postRepository.findByIdForUpdate(postId)
                .orElseThrow(PostsException::new);
        likePostRepository.save(LikePost.builder().post(post).ip(ip).build());
        return post.addLike();
    }

    @Transactional
    public int deleteLiked(Long postId, String ip) {
        log.info("Post deleteLiked 시작 ::: postsId : " + postId + " ip : " + ip);
        Post post = postRepository.findByIdForUpdate(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. postId = " + postId));
        LikePost likePost = likePostRepository.findByPostAndIp(post, ip).get();
        likePostRepository.delete(likePost);
        log.info("Posts deleteLiked 종료, 삭제된 Like : " + likePost.getPost().getLiked());
        return post.deleteLike();
    }

    public LikedResponseDto getLikeStatus(Long postId, String ip) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. postId = " + postId));
        boolean isLikePost = likePostRepository.findByPostAndIp(post, ip).isPresent(); // 게시글 좋아요

        // 해당 게시글의 댓글의 좋아요 리스트
        List<CommentsLikedResponseDto> likedCommentList = likeCommentRepository.findAllByPostAndIp(post, ip)
                .map(list -> list.stream().map(CommentsLikedResponseDto::new)
                .collect(Collectors.toList()))
                .orElse(Collections.emptyList());

        LikedResponseDto likedResponseDto = LikedResponseDto.builder()
                .isLikedPost(isLikePost)
                .likedCommentList(likedCommentList)
                .build();

        log.info("getLikeStatus :::" + likedResponseDto.toString());
        return likedResponseDto;
    }
}
