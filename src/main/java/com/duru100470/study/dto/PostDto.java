package com.duru100470.study.dto;

import java.time.LocalDateTime;
import com.duru100470.study.entity.Member;
import com.duru100470.study.entity.Post;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    @NotBlank
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MemberDto member;
    private int likeCount;
    private int commentCount;

    static public PostDto toDto(Post post) {
        return PostDto.builder()
            .id(post.getId())
            .content(post.getContent())
            .viewCount(post.getViewCount())
            .createdAt(post.getCreatedAt())
            .updatedAt(post.getUpdatedAt())
            .member(MemberDto.toDto(post.getMember()))
            .likeCount(post.getLikes().size())
            .commentCount(post.getComments().size())
            .build();
    }

    public Post toEntity(Member member) {
        return Post.builder()
            .content(content)
            .viewCount(viewCount)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .member(member)
            .build();
    }
}
