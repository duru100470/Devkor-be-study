package com.duru100470.study;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.duru100470.study.controller.PostController;
import com.duru100470.study.dto.MemberDto;
import com.duru100470.study.dto.PostDto;
import com.duru100470.study.security.SecurityUtil;
import com.duru100470.study.service.PostService;
import com.duru100470.study.util.AbstractRestDocsTests;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PostController.class)
public class PostControllerTest extends AbstractRestDocsTests {
    private PostDto postDto;
    private MemberDto memberDto;
    @MockBean
    private PostService postService;
    @MockBean
    private SecurityUtil securityUtil;

    @BeforeEach
    void initPostDto() {
        postDto = PostDto.builder()
            .content("Hello world!")
            .build();

        memberDto = MemberDto.builder()
            .id(1L)
            .username("test@mail.com")
            .nickname("member")
            .build();

        when(securityUtil.getCurrentUsername()).thenReturn("test@mail.com");
    }

    @Test
    public void createPost() throws Exception {
        PostDto response = PostDto.builder()
            .content(postDto.getContent())
            .id(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .member(memberDto)
            .build();

        when(postService.createPost(any(PostDto.class), anyString())).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/posts")
                .content(asJsonString(postDto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(response.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(response.getContent()));
    }

    @Test
    public void getPost() throws Exception {
        PostDto response = PostDto.builder()
            .content(postDto.getContent())
            .id(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .member(memberDto)
            .build();

        when(postService.getPost(1L)).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(response.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(response.getContent()));
    }

    @Test
    public void deletePost() throws Exception {
        PostDto response = PostDto.builder()
            .content(postDto.getContent())
            .id(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .member(memberDto)
            .build();

        when(postService.deletePost(1L, "test@mail.com")).thenReturn(response);

        mockMvc.perform(MockMvcRequestBuilders
                .delete("/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(response.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(response.getContent()));
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}