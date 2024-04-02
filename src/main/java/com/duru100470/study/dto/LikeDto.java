package com.duru100470.study.dto;

import java.time.LocalDateTime;

import com.duru100470.study.entity.Like;
import com.duru100470.study.entity.Member;
import com.duru100470.study.entity.Post;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LikeDto {
    private Long id;
    private MemberDto member;
    private LocalDateTime createdAt;
    private Long postId;

    static public LikeDto toDto(Like like) {
        return LikeDto.builder()
            .id(like.getId())
            .member(MemberDto.toDto(like.getMember()))
            .createdAt(like.getCreatedAt())
            .postId(like.getPost().getId())
            .build();
    }

    public Like toEntity(Member member, Post post) {
        return Like.builder()
            .member(member)
            .createdAt(createdAt)
            .post(post)
            .build();
    }
}
