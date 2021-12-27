package com.yeongho.book.springboot.web;

import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.CommentsRepository;
import com.yeongho.book.springboot.domain.posts.Posts;
import com.yeongho.book.springboot.domain.posts.PostsRepository;
import com.yeongho.book.springboot.service.posts.CommentsService;
import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.CommentsSaveRequestDto;
import com.yeongho.book.springboot.web.dto.CommentsUpdateRequestDto;
import com.yeongho.book.springboot.web.dto.PostsSaveRequestDto;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CommentsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PostsService postsService;

    @Autowired
    private CommentsService commentsService;

    @Before
    public void beforeClear() {
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
    }

    @After
    public void clear() throws Exception {
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
    }

    @Test
    public void Comment_등록된다() throws Exception {
        //given
        // 1. 등록된 post 객체를 가져온다.
        String password = "1234";
        String title = "comment등록테스트post";
        String content = "test";

        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .author("kyh")
                .password(password)
                .content(content)
                .title(title)
                .build();
        String url = "http://localhost:" + port + "/api/v1/posts";
        HttpEntity<PostsSaveRequestDto> postsSaveRequestDtoHttpEntity = new HttpEntity<>(postsSaveRequestDto);
        ResponseEntity<Long> postsSaveRequestDtoResponseEntity = restTemplate.exchange(url, HttpMethod.POST, postsSaveRequestDtoHttpEntity, Long.class);
        Long postId = postsSaveRequestDtoResponseEntity.getBody();

        List<Posts> posts = postsRepository.findAll();

        // 2. parentId, posts obj, author, password, comment 작성
        CommentsSaveRequestDto commentsSaveRequestDto = CommentsSaveRequestDto.builder()
                .parentId(0L)
                .postId(postId)
                .author("kyh")
                .password("123")
                .comment("test comment")
                .build();
        //when
        url = "http://localhost:" + port + "/api/v1/comments";
        HttpEntity<CommentsSaveRequestDto> commentsSaveRequestDtoHttpEntity = new HttpEntity<>(commentsSaveRequestDto);
        ResponseEntity<Long> commentsSaveRequestDtoResponseEntity = restTemplate.exchange(url, HttpMethod.POST, commentsSaveRequestDtoHttpEntity, Long.class);

        //then
        List<Comments> comments = commentsRepository.findAll();
        assertThat(comments.get(0).getId()).isGreaterThan(0L);
        assertThat(comments.get(0).getAuthor()).isEqualTo("kyh");
        assertThat(passwordEncoder.matches("123", comments.get(0).getPassword())).isEqualTo(true);
        assertThat(comments.get(0).getComment()).isEqualTo("test comment");

        assertThat(comments.get(0).getPost().getId()).isEqualTo(posts.get(0).getId());
        assertThat(comments.get(0).getPost().getAuthor()).isEqualTo(posts.get(0).getAuthor());
        assertThat(comments.get(0).getPost().getPassword()).isEqualTo(posts.get(0).getPassword());
        assertThat(comments.get(0).getPost().getTitle()).isEqualTo(posts.get(0).getTitle());
        assertThat(comments.get(0).getPost().getContent()).isEqualTo(posts.get(0).getContent());
    }

    @Test
    public void Comment_수정된다() throws Exception {
        // given
        // post save
        String pw = "123";
        String title = "testTitle";
        String content = "testContent";

        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .author("kyh")
                .password(pw)
                .content(content)
                .title(title)
                .build();
        String url = "http://localhost:" + port + "/api/v1/posts";
        HttpEntity<PostsSaveRequestDto> requestEntity = new HttpEntity<>(postsSaveRequestDto);
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Long.class);

        Long postId = responseEntity.getBody();

        // comments save
        CommentsSaveRequestDto commentsSaveRequestDto = CommentsSaveRequestDto.builder()
                .parentId(0L)
                .postId(postId)
                .author("kyh")
                .password("321")
                .comment("test Comment")
                .build();
        url = "http://localhost:" + port + "/api/v1/comments";
        HttpEntity<CommentsSaveRequestDto> requestEntityOfCommentsSave = new HttpEntity<>(commentsSaveRequestDto);
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntityOfCommentsSave, Long.class);

        // when
        url = "http://localhost:" + port + "/api/v1/comments/" + postId;
        CommentsUpdateRequestDto commentsUpdateRequestDto = CommentsUpdateRequestDto.builder()
                .password("321")
                .comment("update success")
                .build();
        HttpEntity<CommentsUpdateRequestDto> requestEntityOfCommentsUpdate = new HttpEntity<>(commentsUpdateRequestDto);
        responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntityOfCommentsUpdate, Long.class);
        Long commentId = responseEntity.getBody();
        Comments comments = commentsRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException("해당 코멘트가 없습니다. comments id = " + commentId));


        // then
        assertThat("update success").isEqualTo(comments.getComment());
        assertThat(passwordEncoder.matches("321", comments.getPassword())).isEqualTo(true);
    }


    @Test
    public void Comment_삭제된다() throws Exception {
        // given
        // post save
        String pw = "123";
        String title = "testTitle";
        String content = "testContent";

        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .author("kyh")
                .password(pw)
                .content(content)
                .title(title)
                .build();
        String url = "http://localhost:" + port + "/api/v1/posts";
        HttpEntity<PostsSaveRequestDto> requestEntity = new HttpEntity<>(postsSaveRequestDto);
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Long.class);

        Long postId = responseEntity.getBody();

        // comments save
        CommentsSaveRequestDto commentsSaveRequestDto = CommentsSaveRequestDto.builder()
                .parentId(0L)
                .postId(postId)
                .author("kyh")
                .password("321")
                .comment("test Comment")
                .build();
        url = "http://localhost:" + port + "/api/v1/comments";
        HttpEntity<CommentsSaveRequestDto> requestEntityOfCommentsSave = new HttpEntity<>(commentsSaveRequestDto);
        responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntityOfCommentsSave, Long.class);
        Long commentId = responseEntity.getBody();

        // when
        url = "http://localhost:" + port + "/api/v1/comments/" + commentId;
        HttpHeaders headers = new HttpHeaders();
        HttpEntity entity = new HttpEntity(headers);
        responseEntity = restTemplate.exchange(url, HttpMethod.DELETE, entity, Long.class);


        // then
        assertThat(commentsRepository.findById(commentId))
                .isEmpty();
    }
}
