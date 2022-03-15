package com.yeongho.book.springboot.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongho.book.springboot.domain.posts.Post;
import com.yeongho.book.springboot.domain.posts.PostRepository;
import com.yeongho.book.springboot.exception.PostsException;
import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.*;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostApiControllerTest {
    private Long postId;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostsService postsService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void init() throws Exception {
        Map<String,String> data = new HashMap<>();
        data.put("author", "author");
        data.put("password", "123");
        data.put("title", "title");
        data.put("content", "content");

        MockMultipartFile image = new MockMultipartFile("file", "image.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("file", "image2.png", "image/png",
                "<<png data>>".getBytes());
//        List<MockMultipartFile> mockMultipartFiles = new ArrayList<>();
//        mockMultipartFiles.add(image);
//        mockMultipartFiles.add(image2);

        String content = objectMapper.writeValueAsString(data);

        MockMultipartFile json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        postId = Long.parseLong(mockMvc.perform(multipart("/api/v1/posts").file(json).file(image).file(image2).contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
    }
    @After
    public void clear() throws Exception {
        postRepository.deleteAll();
    }

    @Transactional
    @Test
    public void 게시물등록() throws Exception {
        //when
        List<Post> postList = postRepository.findAll();

        //then
        assertThat("title").isEqualTo(postList.get(0).getTitle());
        assertThat("author").isEqualTo(postList.get(0).getAuthor());
        assertThat("content").isEqualTo(postList.get(0).getContent());
        assertThat("image.png").isEqualTo(postList.get(0).getFileItem().get(0).getOriginFileName());
        assertThat("image2.png").isEqualTo(postList.get(0).getFileItem().get(1).getOriginFileName());
    }

    @Test
    @Transactional
    public void 게시물수정() throws Exception {
        //when
        log.info("게시물 수정 시작");

        Map<String,String> updatedData = new HashMap<>();
        updatedData.put("author", "author");
        updatedData.put("password", "123");
        updatedData.put("title", "titleUpdate");
        updatedData.put("content", "contentUpdate");

        MockMultipartFile updatedImage = new MockMultipartFile("file", "updatedImage.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile updatedImage2 = new MockMultipartFile("file", "updatedImage2.png", "image/png",
                "<<png data>>".getBytes());

        String updatedContent = objectMapper.writeValueAsString(updatedData);

        MockMultipartFile updatedJson = new MockMultipartFile("data", "jsonData", "application/json",
                updatedContent.getBytes(StandardCharsets.UTF_8));

        log.info("파일 업데이트 시작");
        // call update API
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/posts/" + postId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        Long updatedPostId = Long.parseLong(mockMvc.perform(builder.file(updatedJson).file(updatedImage).file(updatedImage2).contentType("multipart/mixed")
        .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        Post post = postRepository.findById(updatedPostId).orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다." + updatedPostId));

        //then
        assertThat(updatedData.get("title")).isEqualTo(post.getTitle());
        assertThat(updatedData.get("content")).isEqualTo(post.getContent());
        assertThat(post.getFileItem().stream().count()).isEqualTo(2);
//        assertThat("updatedImage.png").isEqualTo(post.getFileItem().get(0).getOriginFileName());
//        assertThat("updatedImage2.png").isEqualTo(post.getFileItem().get(1).getOriginFileName());
    }

    @Test
    @Transactional
    public void 게시물_파일빈값으로_수정() throws Exception {
        //when
        log.info("게시물 수정 시작");

        Map<String,String> updatedData = new HashMap<>();
        updatedData.put("author", "author");
        updatedData.put("password", "123");
        updatedData.put("title", "titleUpdate");
        updatedData.put("content", "contentUpdate");

        String updatedContent = objectMapper.writeValueAsString(updatedData);

        MockMultipartFile updatedJson = new MockMultipartFile("data", "jsonData", "application/json",
                updatedContent.getBytes(StandardCharsets.UTF_8));

        log.info("파일 업데이트 시작");
        // call update API
        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/api/v1/posts/" + postId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        Long updatedPostId = Long.parseLong(mockMvc.perform(builder.file(updatedJson).contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        Post post = postRepository.findById(updatedPostId).orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다." + updatedPostId));

        //then
        assertThat(updatedData.get("title")).isEqualTo(post.getTitle());
        assertThat(updatedData.get("content")).isEqualTo(post.getContent());
        assertThat(post.getFileItem().stream().count()).isEqualTo(0);
//        assertThat("updatedImage.png").isEqualTo(post.getFileItem().get(0).getOriginFileName());
//        assertThat("updatedImage2.png").isEqualTo(post.getFileItem().get(1).getOriginFileName());
    }

    @Test
    public void 게시물삭제() throws Exception {
        //when
        Map<String,String> data = new HashMap<>();
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 존재하지 않습니다." + postId));

        data.clear();
        data.put("password", "123");
        String content = objectMapper.writeValueAsString(data);

        this.mockMvc.perform(post("/api/v1/posts/" + post.getId())
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        //then
        assertThatIllegalArgumentException().isThrownBy(() -> postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 존재하지 않습니다." + postId)));
    }

    @Test
    public void 제목_검색기능() throws Exception {
        //given
        Map<String,String> data = new HashMap<>();
        data.put("author", "author");
        data.put("password", "123");
        data.put("title", "title123");
        data.put("content", "content");

        String content = objectMapper.writeValueAsString(data);
        MockMultipartFile json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        content = objectMapper.writeValueAsString(data);
        json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/posts").file(json).contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk());

        //when
        String result = mockMvc.perform(get("/api/v1/posts/search?" + "searchType=title&keyword=title")).andReturn().getResponse().getContentAsString();
        log.info("제목 검색 결과 : " + result);
        List<PostsListResponseDto> resultList = objectMapper.readValue(result, new TypeReference<List<PostsListResponseDto>>(){});

        //then
        assertThat(resultList.size()).isGreaterThan(0);
    }

    @Test
    public void 내용_검색기능() throws Exception {
        //when
        String result = mockMvc.perform(get("/api/v1/posts/search?" + "searchType=content&keyword=content")).andReturn().getResponse().getContentAsString();
        log.info("내용_검색기능 결과 : " + result);
        List<PostsListResponseDto> resultList = objectMapper.readValue(result, new TypeReference<List<PostsListResponseDto>>(){});

        //then
        assertThat(resultList.size()).isGreaterThan(0);
    }

    @Test
    public void 작성자_검색기능() throws Exception {
        //when
        String result = mockMvc.perform(get("/api/v1/posts/search?" + "searchType=author&keyword=author")).andReturn().getResponse().getContentAsString();
        log.info("작성자_검색기능 결과 : " + result);
        List<PostsListResponseDto> resultList = objectMapper.readValue(result, new TypeReference<List<PostsListResponseDto>>(){});

        //then
        assertThat(resultList.size()).isGreaterThan(0);
    }

    @Test
    public void 좋아요_조회기능() throws Exception {
        String ip = "127.0.0.1";
        String content = objectMapper.writeValueAsString(new LikedRequestDto(ip));
        mockMvc.perform(post("/api/v1/posts/like/" + postId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)); // like + 1
        String result = mockMvc.perform(get("/api/v1/posts/like/" + postId)) // get liked count
                .andReturn().getResponse().getContentAsString();
        LikedDto likedDto = objectMapper.readValue(result, LikedDto.class);
        log.info("LikeDto ::: ", likedDto.toString());
        assertThat(likedDto.getLiked()).isEqualTo(1);
    }

    @Test
    public void 좋아요_추가기능() throws Exception {
        // given
        String ip = "127.0.0.1";

        // when
        String content = objectMapper.writeValueAsString(new LikedRequestDto(ip));
        mockMvc.perform(post("/api/v1/posts/like/" + postId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        String result = mockMvc.perform(post("/api/v1/posts/like/" + postId)
                .content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        // then
        Post post = postRepository.findById(postId).orElseThrow(PostsException::new);
        LikedDto likedDto = objectMapper.readValue(result, LikedDto.class);
        log.info("result :" + result);
        log.info("LikeDto ::: " + likedDto.getLiked());
        assertThat(likedDto.getLiked()).isEqualTo(2);
        assertThat(post.getLiked()).isEqualTo(2);
    }

    @Test
    public void 좋아요_삭제기능() throws Exception {
        String ip = "127.0.0.1";
        String content = objectMapper.writeValueAsString(new LikedRequestDto(ip));
        mockMvc.perform(post("/api/v1/posts/like/" + postId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));
        String result = mockMvc.perform(post("/api/v1/posts/unlike/" + postId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString(); // 좋아요 삭제
        log.info("result :" + result);
        LikedDto likedDto = objectMapper.readValue(result, LikedDto.class);
        log.info("LikeDto ::: " + likedDto.getLiked());
        assertThat(likedDto.getLiked()).isEqualTo(0);
    }

    @Test
    @DisplayName("좋아요 api를 10번 호출하는 경우 좋아요 갯수가 10개가 되어야 한다.")
    public void 좋아요추가_동시성이슈() throws Exception {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);

        // when
        for (int i = 0; i < 10; i++) {
            int ip = i;
            executorService.execute(() -> {
                try {
                    String content = objectMapper.writeValueAsString(new LikedRequestDto(Integer.toString(1+ip)));

                    mockMvc.perform(post("/api/v1/posts/like/" + postId)
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON));
                } catch (Exception e) {
                    log.error(e);
                }
                latch.countDown();
            });
        }
        latch.await();
        executorService.shutdown();

        Post post = postRepository.findById(postId).orElseThrow(PostsException::new);
        assertThat(post.getLiked()).isEqualTo(10);
    }

    @Test
    @DisplayName("좋아요 삭제 api를 5번 호출하는 경우 좋아요 갯수가 0개가 되어야 한다.")
    public void 좋아요삭제_동시성이슈() throws Exception {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            int ip = i;
            executorService.execute(() -> {
                try {
                    String content = objectMapper.writeValueAsString(new LikedRequestDto(Integer.toString(1+ip)));
                    mockMvc.perform(post("/api/v1/posts/like/" + postId)
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON));
                } catch (Exception e) {
                    log.error(e);
                }
                latch.countDown();
            });
        }
        latch.await();
//        executorService.shutdown();

        // when
        CountDownLatch deleteLatch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            int ip = i;
            executorService.execute(() -> {
                try {
                    String content = objectMapper.writeValueAsString(new LikedRequestDto(Integer.toString(1+ip)));
                    mockMvc.perform(post("/api/v1/posts/unlike/" + postId)
                            .content(content)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON));
                }  catch (Exception e) {
                    log.error(e);
                }
                deleteLatch.countDown();
            });
        }
        deleteLatch.await();
        executorService.shutdown();

        Post post = postRepository.findById(postId).orElseThrow(PostsException::new);
        assertThat(post.getLiked()).isEqualTo(0);
    }
}
