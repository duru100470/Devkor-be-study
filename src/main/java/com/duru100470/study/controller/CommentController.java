package com.duru100470.study.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duru100470.study.dto.CommentDto;
import com.duru100470.study.security.SecurityUtil;
import com.duru100470.study.service.PostService;

import jakarta.validation.Valid;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Slf4j
public class CommentController {
    private final PostService postService;
    private final SecurityUtil securityUtil;

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id, @Valid @RequestBody CommentDto commentDto) {
        String writer = securityUtil.getCurrentUsername();

        CommentDto result = postService.updateComment(id, commentDto, writer);
        
        return ResponseEntity
            .created(URI.create("/" + id + "/comments"))
            .body(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<CommentDto> deleteComment(@PathVariable Long id) {
        String writer = securityUtil.getCurrentUsername();

        return ResponseEntity.ok(postService.deleteComment(id, writer));
    }
}
