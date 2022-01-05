package com.yeongho.book.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongho.book.springboot.domain.posts.Posts;
import com.yeongho.book.springboot.domain.posts.PostsRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class PostApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PostsRepository postsRepository;


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @After
    public void clear() throws Exception {
        postsRepository.deleteAll();
    }

    @Transactional
    @Test
    public void 게시물등록() throws Exception {
        //given
        Map<String,String> data = new HashMap<>();
        data.put("author", "author");
        data.put("password", "123");
        data.put("title", "title");
        data.put("content", "content");

        MockMultipartFile image = new MockMultipartFile("file", "image.png", "image/png",
                "<<png data>>".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("file", "image2.png", "image/png",
                "<<png data>>".getBytes());
        List<MockMultipartFile> mockMultipartFiles = new ArrayList<>();
        mockMultipartFiles.add(image);
        mockMultipartFiles.add(image2);

        String content = objectMapper.writeValueAsString(data);

        MockMultipartFile json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/posts").file(image).file(image2).file(json).contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk());

        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        assertThat("title").isEqualTo(postsList.get(0).getTitle());
        assertThat("author").isEqualTo(postsList.get(0).getAuthor());
        assertThat("content").isEqualTo(postsList.get(0).getContent());
        assertThat("image.png").isEqualTo(postsList.get(0).getFileItem().get(0).getOriginFileName());
        assertThat("image2.png").isEqualTo(postsList.get(0).getFileItem().get(1).getOriginFileName());
    }

    @Test
    @Transactional
    public void 게시물수정() throws Exception {
        //given
        System.out.println("### 게시물 신규 추가");

        Map<String,String> data = new HashMap<>();
        data.put("author", "author");
        data.put("password", "123");
        data.put("title", "title");
        data.put("content", "content");

        MockMultipartFile image = new MockMultipartFile("file", "image.png", "image/png",
                "<<png data>>".getBytes());
        String content = objectMapper.writeValueAsString(data);

        MockMultipartFile json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        Long postId = Long.parseLong(mockMvc.perform(multipart("/api/v1/posts").file(json).contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        //when
        System.out.println("### 게시물 수정 시작");

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

        System.out.println("파일 업데이트 시작");
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

        Posts post = postsRepository.findById(updatedPostId).orElseThrow(() -> new IllegalArgumentException("게시물이 존재하지 않습니다." + updatedPostId));

        //then
        assertThat(updatedData.get("title")).isEqualTo(post.getTitle());
        assertThat(updatedData.get("content")).isEqualTo(post.getContent());
        assertThat(post.getFileItem()).isEmpty();
//        assertThat("updatedImage.png").isEqualTo(post.getFileItem().get(0).getOriginFileName());
//        assertThat("updatedImage2.png").isEqualTo(post.getFileItem().get(1).getOriginFileName());
    }

    @Test
    public void 게시물삭제() throws Exception {
        //given
        Map<String,String> data = new HashMap<>();
        data.put("author", "author");
        data.put("password", "123");
        data.put("title", "title");
        data.put("content", "content");

        MockMultipartFile image = new MockMultipartFile("file", "image.png", "image/png",
                "<<png data>>".getBytes());
        String content = objectMapper.writeValueAsString(data);

        MockMultipartFile json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        Long postId = Long.parseLong(mockMvc.perform(multipart("/api/v1/posts").file(image).file(json).contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());

        Posts post = postsRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 존재하지 않습니다." + postId));
        //when
        this.mockMvc.perform(delete("/api/v1/posts/" + post.getId()))
                .andExpect(status().isOk());


        //then
        assertThatIllegalArgumentException().isThrownBy(() -> postsRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 게시물이 존재하지 않습니다." + postId)));
    }

}
