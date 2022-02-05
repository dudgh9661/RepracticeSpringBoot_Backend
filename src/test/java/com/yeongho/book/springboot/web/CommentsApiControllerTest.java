package com.yeongho.book.springboot.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongho.book.springboot.domain.posts.Comments;
import com.yeongho.book.springboot.domain.posts.CommentsRepository;
import com.yeongho.book.springboot.domain.posts.Posts;
import com.yeongho.book.springboot.domain.posts.PostsRepository;
import com.yeongho.book.springboot.web.dto.CommentsResponseDto;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log4j2
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class CommentsApiControllerTest {

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private CommentsRepository commentsRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Posts posts;

    @Before
    public void beforeClear() throws Exception {
        log.info("comment Test 초기화 deleteAll() 시작");
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
        log.info("comment Test 초기화 deleteAll() 종료");

        // 댓글 테스트를 위해 게시글 작성이 선행되어야 한다.
        Map<String, Object> data = new HashMap<>();
        data.put("author", "kyh");
        data.put("password", "1234");
        data.put("title", "post");
        data.put("content", "test");

        String content = objectMapper.writeValueAsString(data);
        MockMultipartFile json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/v1/posts").file(json).contentType("multipart/mixed")
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8")).andExpect(status().isOk());

        posts = postsRepository.findAll().get(0);
    }

    @After
    public void clear() throws Exception {
        commentsRepository.deleteAll();
        postsRepository.deleteAll();
    }

    @Transactional
    @Test
    public void Comment_등록된다() throws Exception {
        //given
        // 0. 게시글을 등록한다.
        Map<String, Object> data = new HashMap<>();

        //when
        // 2. Comment 저장 요청
        data.clear();
        data.put("parentId", "0");
        data.put("postId", posts.getId());
        data.put("author", "kyh2");
        data.put("password", "123");
        data.put("text", "test");

        String content = objectMapper.writeValueAsString(data);

        mockMvc.perform(post("/api/v1/comments")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        //then
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/comments/" + posts.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        List<CommentsResponseDto> commentsResponseDto = objectMapper.readValue(result, new TypeReference<List<CommentsResponseDto>>(){});
        CommentsResponseDto comment = commentsResponseDto.get(0);
        assertThat(comment.getAuthor()).isEqualTo("kyh2");
        assertThat(comment.getText()).isEqualTo("test");
    }

    @Test
    public void Comment_수정된다() throws Exception {
        // given
        // 댓글 저장
        Map<String, Object> data = new HashMap<>();
        data.put("parentId", 0);
        data.put("postId", posts.getId());
        data.put("author", "kyh2");
        data.put("password", "123");
        data.put("text", "test");

        String content = objectMapper.writeValueAsString(data);

        mockMvc.perform(post("/api/v1/comments")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        // when
        // 댓글 수정
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("password", "123");
        updatedData.put("text", "updatedComment");

        content = objectMapper.writeValueAsString(updatedData);
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/comments/" + posts.getId())
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();
        Long commentsId = Long.parseLong(mvcResult.getResponse().getContentAsString());
        Comments comments = commentsRepository.findById(commentsId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id : "+ commentsId));

        // then
        assertThat(comments.getText()).isEqualTo("updatedComment");
    }


    @Test
    public void Comment_삭제된다() throws Exception {
        // given
        // post save
        // comments save
        Map<String, Object> data = new HashMap<>();
        data.put("parentId", 0);
        data.put("postId", posts.getId());
        data.put("author", "kyh2");
        data.put("password", "123");
        data.put("text", "commentDeleteTest");

        String content = objectMapper.writeValueAsString(data);

        String commentStrId = mockMvc.perform(post("/api/v1/comments")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long commentId = Long.parseLong(commentStrId);

        // when
        data.clear();
        data.put("password", "123");
        content = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/api/v1/comments/"  + commentId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        // then
        String result = mockMvc.perform(get("/api/v1/comments/" + posts.getId())).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<CommentsResponseDto> responseData = objectMapper.readValue(result, new TypeReference<List<CommentsResponseDto>>(){});
        assertThat(responseData.get(0).getIsDeleted()).isEqualTo(true);
    }
}
