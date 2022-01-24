package com.yeongho.book.springboot.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeongho.book.springboot.exception.ErrorResponse;
import com.yeongho.book.springboot.exception.InvalidPasswordException;
import com.yeongho.book.springboot.service.posts.PostsService;
import com.yeongho.book.springboot.web.dto.PostsSaveRequestDto;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.omg.CORBA.DynAnyPackage.Invalid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ExceptionHandlerControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    private PostsApiController postsApiController;

    @Mock
    private PostsService postsService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(postsApiController).setControllerAdvice(new ExceptionHandlerController()).build();
    }

    @Test
    public void IOException_예외처리_테스트() throws Exception {
        // given
        PostsSaveRequestDto postsSaveRequestDto = PostsSaveRequestDto.builder()
                .author("author")
                .password("123")
                .title("title")
                .content("content")
                .build();

        // when
        when(postsApiController.save(any(), any())).thenThrow(new IOException());

        String content = objectMapper.writeValueAsString(postsSaveRequestDto);
        MockMultipartFile json = new MockMultipartFile("data", "jsonData", "application/json",
                content.getBytes(StandardCharsets.UTF_8));

        // then
        String result = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts")
                .file(json).contentType("multipart/mixed").accept(MediaType.APPLICATION_JSON).characterEncoding("UTF-8"))
        .andReturn().getResponse().getContentAsString();

        ErrorResponse errorResponse = objectMapper.readValue(result, ErrorResponse.class);
        Assertions.assertThat(errorResponse.getStatusCode()).isEqualTo(500);
        Assertions.assertThat(errorResponse.getMessage()).isEqualTo("문제가 발생했습니다. 시스템 관리자에게 문의해주세요.");
    }
}
