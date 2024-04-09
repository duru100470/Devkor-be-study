package com.duru100470.study.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duru100470.study.entity.Comment;
import com.duru100470.study.entity.Post;

import java.util.List;


public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost(Post post);
}
