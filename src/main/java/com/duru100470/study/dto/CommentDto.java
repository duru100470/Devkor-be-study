package com.duru100470.study.dto;

import java.time.LocalDateTime;

import com.duru100470.study.entity.Comment;
import com.duru100470.study.entity.Member;
import com.duru100470.study.entity.Post;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDto {
    private Long id;
    @NotBlank
    private String content;
    private MemberDto member;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private Long parentId;
    private Long postId;

    static public CommentDto toDto(Comment comment) {
        Long parentId = null;

        if (comment.getParentComment() != null)
            parentId = comment.getParentComment().getId();

        return CommentDto.builder()
            .id(comment.getId())
            .content(comment.getContent())
            .member(MemberDto.toDto(comment.getMember()))
            .createdAt(comment.getCreatedAt())
            .updatedAt(comment.getUpdatedAt())
            .isDeleted(comment.getIsDeleted())
            .parentId(parentId)
            .postId(comment.getPost().getId())
            .build();
    }

    public Comment toEntity(Member member, Comment parent, Post post) {
        return Comment.builder()
            .content(content)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .isDeleted(isDeleted)
            .member(member)
            .parentComment(parent)
            .post(post)
            .build();
    }
}
