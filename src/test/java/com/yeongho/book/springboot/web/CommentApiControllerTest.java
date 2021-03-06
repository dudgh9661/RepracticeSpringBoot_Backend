package com.yeongho.book.springboot.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongho.book.springboot.domain.posts.Comment;
import com.yeongho.book.springboot.domain.posts.CommentRepository;
import com.yeongho.book.springboot.domain.posts.Post;
import com.yeongho.book.springboot.domain.posts.PostRepository;
import com.yeongho.book.springboot.web.dto.CommentsResponseDto;
import com.yeongho.book.springboot.web.dto.CommentsSaveRequestDto;
import com.yeongho.book.springboot.web.dto.PostsResponseDto;
import com.yeongho.book.springboot.web.dto.PostsSaveRequestDto;
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
public class CommentApiControllerTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Post post;

    @Before
    public void beforeClear() throws Exception {
        log.info("comment Test ????????? deleteAll() ??????");
        commentRepository.deleteAll();
        postRepository.deleteAll();
        log.info("comment Test ????????? deleteAll() ??????");

        // ?????? ???????????? ?????? ????????? ????????? ??????????????? ??????.
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

        post = postRepository.findAll().get(0);
    }

    @After
    public void clear() throws Exception {
        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Transactional
    @Test
    public void Comment_????????????() throws Exception {
        //given
        // 0. ???????????? ????????????.
        Map<String, Object> data = new HashMap<>();

        //when
        // 2. Comment ?????? ??????
        data.clear();
        data.put("parentId", "0");
        data.put("postId", post.getId());
        data.put("author", "kyh2");
        data.put("password", "123");
        data.put("text", "test");
        data.put("isDeleted", false);

        String content = objectMapper.writeValueAsString(data);

        mockMvc.perform(post("/api/v1/comments")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        //then
        MvcResult mvcResult = mockMvc.perform(get("/api/v1/comments/" + post.getId()))
                .andExpect(status().isOk())
                .andReturn();

        String result = mvcResult.getResponse().getContentAsString();
        List<CommentsResponseDto> commentsResponseDto = objectMapper.readValue(result, new TypeReference<List<CommentsResponseDto>>(){});
        CommentsResponseDto comment = commentsResponseDto.get(0);
        assertThat(comment.getAuthor()).isEqualTo("kyh2");
        assertThat(comment.getText()).isEqualTo("test");
    }

    @Test
    public void Comment_????????????() throws Exception {
        // given
        // ?????? ??????
        Map<String, Object> data = new HashMap<>();
        data.put("parentId", 0);
        data.put("postId", post.getId());
        data.put("author", "kyh2");
        data.put("password", "123");
        data.put("text", "test");
        data.put("isDeleted", false);

        String content = objectMapper.writeValueAsString(data);

        mockMvc.perform(post("/api/v1/comments")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        // when
        // ?????? ??????
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("password", "123");
        updatedData.put("text", "updatedComment");

        content = objectMapper.writeValueAsString(updatedData);
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/comments/" + post.getId())
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn();
        Long commentsId = Long.parseLong(mvcResult.getResponse().getContentAsString());
        Comment comment = commentRepository.findById(commentsId)
                .orElseThrow(() -> new IllegalArgumentException("?????? ????????? ???????????? ????????????. id : "+ commentsId));

        // then
        assertThat(comment.getText()).isEqualTo("updatedComment");
    }


    @Test
    public void Comment_????????????() throws Exception {
        // given
        // post save
        // comments save
        Map<String, Object> data = new HashMap<>();
        data.put("parentId", 0);
        data.put("postId", post.getId());
        data.put("author", "kyh2");
        data.put("password", "123");
        data.put("text", "commentDeleteTest");
        data.put("isDeleted", false);

        String content = objectMapper.writeValueAsString(data);

        String commentObj = mockMvc.perform(post("/api/v1/comments")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CommentsResponseDto commentsResponseDto = objectMapper.readValue(commentObj, CommentsResponseDto.class);
        Long commentId = commentsResponseDto.getId();
        // when
        data.clear();
        data.put("password", "123");
        data.put("isDeleted", true);
        content = objectMapper.writeValueAsString(data);
        mockMvc.perform(post("/api/v1/comments/"  + commentId)
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk());

        // then
        String result = mockMvc.perform(get("/api/v1/comments/" + post.getId())).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<CommentsResponseDto> responseData = objectMapper.readValue(result, new TypeReference<List<CommentsResponseDto>>(){});
        assertThat(responseData.get(0).getIsDeleted()).isEqualTo(true);
    }



    @Test
    @DisplayName("????????? ????????? ????????? ???, ?????? ????????? ?????? ????????????")
    public void ????????????_?????????_?????????_????????????() throws Exception {
        // given
        String postId = Long.toString(post.getId());
        CommentsSaveRequestDto commentsSaveRequestDto = CommentsSaveRequestDto.builder()
                .parentId("0")
                .postId(postId)
                .author("commentAuthor")
                .text("commentText")
                .password("123")
                .isDeleted(false)
                .build();

        String content = objectMapper.writeValueAsString(commentsSaveRequestDto);

        mockMvc.perform(post("/api/v1/comments")
                .content(content)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // when
        String res = mockMvc.perform(get("/api/v1/posts/" + postId)).andReturn().getResponse().getContentAsString();
        PostsResponseDto postsResponseDto = objectMapper.readValue(res, PostsResponseDto.class);
        CommentsResponseDto commentsResponseDto = postsResponseDto.getCommentList().get(0);
        // then
        assertThat(postsResponseDto.getAuthor()).isEqualTo("kyh");
        assertThat(postsResponseDto.getContent()).isEqualTo("test");
        assertThat(postsResponseDto.getTitle()).isEqualTo("post");

        assertThat(commentsResponseDto.getAuthor()).isEqualTo("commentAuthor");
        assertThat(commentsResponseDto.getText()).isEqualTo("commentText");
    }
}
