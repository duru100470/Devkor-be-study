package com.duru100470.study.controller;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.duru100470.study.dto.CommentDto;
import com.duru100470.study.dto.LikeDto;
import com.duru100470.study.dto.PostDto;
import com.duru100470.study.security.SecurityUtil;
import com.duru100470.study.service.PostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody PostDto postDto) {
        String writer = securityUtil.getCurrentUsername();
        PostDto result = postService.createPost(postDto, writer);

        return ResponseEntity
            .created(URI.create("/posts/" + result.getId()))
            .body(result);
    }

    // @GetMapping("/list")
    // public ResponseEntity<List<PostDto>> getPosts() {
    //     return ResponseEntity.ok(postService.getPosts());
    // }
    
    @GetMapping("/list")
    public ResponseEntity<Page<PostDto>> getPosts(Model model, 
            @RequestParam(value="page", defaultValue = "0") int page,
            @RequestParam(value="sortBy", defaultValue = "date") String order,
            @RequestParam(value="search", required = false) String search) {
        return ResponseEntity.ok(postService.getPostPage(page, order, search));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDto> getPost(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDto> updatePost(@PathVariable("id") Long id, @Valid @RequestBody PostDto postDto) {
        String writer = securityUtil.getCurrentUsername();
        PostDto result = postService.updatePost(id, postDto, writer);

        return ResponseEntity
            .created(URI.create("/posts/" + result.getId()))
            .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PostDto> deletePost(@PathVariable("id") Long id) {
        String writer = securityUtil.getCurrentUsername();

        return ResponseEntity.ok(postService.deletePost(id, writer));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable("id") Long id, @Valid @RequestBody CommentDto commentDto) {
        String writer = securityUtil.getCurrentUsername();

        commentDto.setPostId(id);

        return ResponseEntity
            .created(URI.create("/" + id + "/comments"))
            .body(postService.createComment(commentDto, writer));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDto>> getComments(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.getComments(id));
    }

    @PostMapping("/{id}/likes")
    public ResponseEntity<LikeDto> addLike(@PathVariable("id") Long id) {
        String writer = securityUtil.getCurrentUsername();

        LikeDto likeDto = LikeDto.builder()
            .postId(id)
            .build();

        return ResponseEntity.ok(postService.addLike(likeDto, writer));
    }

    @DeleteMapping("/{id}/likes")
    public ResponseEntity<LikeDto> deleteLike(@PathVariable("id") Long id) {
        String writer = securityUtil.getCurrentUsername();

        LikeDto likeDto = LikeDto.builder()
            .postId(id)
            .build();

        return ResponseEntity.ok(postService.deleteLike(likeDto, writer));
    }
}
