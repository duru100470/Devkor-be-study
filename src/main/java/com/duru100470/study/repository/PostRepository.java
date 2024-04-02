package com.duru100470.study.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.duru100470.study.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllSortedByCreatedAtDesc();
    
    @Query("SELECT p FROM Post p ORDER BY p.viewCount DESC")
    List<Post> findAllSortedByViewCountDesc();

    @Query(value = """
        SELECT p.*
        FROM post p
        LEFT JOIN (
            SELECT post_id, COUNT(id) AS like_count
            FROM post_like
            GROUP BY post_id
        ) pl ON p.post_id = pl.post_id
        ORDER BY pl.like_count DESC, p.post_id;
        """, nativeQuery = true)
    List<Post> findAllSortedByLikesCount();
}