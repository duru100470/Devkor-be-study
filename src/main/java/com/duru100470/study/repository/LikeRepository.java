package com.duru100470.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duru100470.study.entity.Like;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findFirstByMemberIdAndPostId(Long memberId, Long postId);
}
