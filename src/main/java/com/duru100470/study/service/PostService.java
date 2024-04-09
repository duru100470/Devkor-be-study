package com.duru100470.study.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.jaxb.SpringDataJaxb.PageDto;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duru100470.study.dto.*;
import com.duru100470.study.entity.*;
import com.duru100470.study.error.*;
import com.duru100470.study.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Transactional
    public PostDto createPost(PostDto postDto, String writer) {
        Member member = memberRepository.findByUsername(writer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        postDto.setMember(MemberDto.toDto(member));
        postDto.setCreatedAt(LocalDateTime.now());
        postDto.setUpdatedAt(LocalDateTime.now());

        Post post = postDto.toEntity(member);
        postRepository.save(post);

        postDto.setId(post.getId());
        return postDto;
    }

    @Transactional
    public List<PostDto> getPosts() {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
            .map(dto -> PostDto.toDto(dto))
            .toList();
    }
    
    @Transactional
    public Page<PostDto> getPostPage(int page, String order, String search) {
        PageRequest pageRequest = PageRequest.of(page, 10);
        Stream<Post> query = null;

        switch (order) {
            case "date":
                query = postRepository.findAllSortedByCreatedAtDesc().stream();
                break;
            case "views":
                query = postRepository.findAllSortedByViewCountDesc().stream();
                break;
            case "likes":
                query = postRepository.findAllSortedByLikesCount().stream();
                break;
            default:
                throw new ApiException(ExceptionEnum.INTERNAL_SERVER_ERROR, "잘못된 형식입니다.");
        }

        if (search != null && !search.isEmpty() && !search.isBlank()) {
            query = query
                .filter(p -> p.getContent().contains(search));
        }

        List<Post> list = query.toList();
        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), list.size());

        if (list.size() <= start) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "잘못된 형식입니다.");
        }

        Page<Post> pages = new PageImpl<Post>(list.subList(start, end), pageRequest, list.size());
        return pages.map(e -> PostDto.toDto(e));
    }

    @Transactional
    public PostDto getPost(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "게시물을 찾을 수 없습니다."));

        post.setViewCount(post.getViewCount() + 1);
        postRepository.save(post);

        return PostDto.toDto(post);
    }

    @Transactional
    public PostDto updatePost(Long id, PostDto postDto, String executer) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "게시물을 찾을 수 없습니다."));

        Member owner = memberRepository.findByUsername(executer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        if (owner.getId() != post.getMember().getId()) {
            throw new ApiException(ExceptionEnum.FORBIDDEN_EXCEPTION, "권한이 없습니다.");
        }

        post.setContent(postDto.getContent());
        post.setUpdatedAt(LocalDateTime.now());
        postRepository.save(post);

        return PostDto.toDto(post);
    }

    @Transactional
    public PostDto deletePost(Long id, String executer) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "게시물을 찾을 수 없습니다."));
        PostDto result = PostDto.toDto(post);

        Member owner = memberRepository.findByUsername(executer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        if (owner.getId() != post.getMember().getId()) {
            throw new ApiException(ExceptionEnum.FORBIDDEN_EXCEPTION, "권한이 없습니다.");
        }
        
        postRepository.deleteById(id);
        return result;
    }

    @Transactional
    public CommentDto createComment(CommentDto commentDto, String writer) {
        Member member = memberRepository.findByUsername(writer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        Post post = postRepository.findById(commentDto.getPostId())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "게시물을 찾을 수 없습니다."));

        Comment parent = null;

        if (commentDto.getParentId() != null) {
            parent = commentRepository.findById(commentDto.getParentId())
                .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "댓글을 찾을 수 없습니다."));
        }

        commentDto.setMember(MemberDto.toDto(member));
        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setUpdatedAt(LocalDateTime.now());

        Comment comment = commentDto.toEntity(member, parent, post);
        commentRepository.save(comment);

        commentDto.setId(comment.getId());
        return commentDto;
    }

    @Transactional
    public CommentDto deleteComment(Long id, String executer) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "댓글을 찾을 수 없습니다."));

        CommentDto result = CommentDto.toDto(comment);

        Member owner = memberRepository.findByUsername(executer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        if (owner.getId() != comment.getMember().getId()) {
            throw new ApiException(ExceptionEnum.FORBIDDEN_EXCEPTION, "권한이 없습니다.");
        }

        comment.setContent("");
        comment.setIsDeleted(true);
        
        commentRepository.save(comment);
        return result;
    }

    @Transactional
    public CommentDto updateComment(Long id, CommentDto commentDto, String executer) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "댓글을 찾을 수 없습니다."));

        Member owner = memberRepository.findByUsername(executer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        if (owner.getId() != comment.getMember().getId()) {
            throw new ApiException(ExceptionEnum.FORBIDDEN_EXCEPTION, "권한이 없습니다.");
        }

        if (comment.getIsDeleted()) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "삭제된 댓글입니다.");
        }

        comment.setContent(commentDto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(comment);

        return CommentDto.toDto(comment);
    }

    @Transactional
    public List<CommentDto> getComments(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "게시물을 찾을 수 없습니다."));

        List<Comment> comments = commentRepository.findByPost(post);

        return comments.stream()
            .map(dto -> CommentDto.toDto(dto))
            .toList();
    }

    @Transactional
    public LikeDto addLike(LikeDto likeDto, String executer) {
        Member owner = memberRepository.findByUsername(executer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        if (likeRepository.findFirstByMemberIdAndPostId(owner.getId(), likeDto.getPostId()).isPresent()) {
            throw new ApiException(ExceptionEnum.DUPLICATE_DATA_EXCEPTION, "중복된 요청입니다.");
        }

        Post post = postRepository.findById(likeDto.getPostId())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "게시물을 찾을 수 없습니다."));

        likeDto.setCreatedAt(LocalDateTime.now());
        Like like = likeDto.toEntity(owner, post);
        likeRepository.save(like);

        return LikeDto.toDto(like);
    }

    @Transactional
    public LikeDto deleteLike(LikeDto likeDto, String executer) {
        Member owner = memberRepository.findByUsername(executer)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        Like like = likeRepository.findFirstByMemberIdAndPostId(owner.getId(), likeDto.getPostId())
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "이미 취소된 좋아요입니다."));

        LikeDto result = LikeDto.toDto(like);
        likeRepository.delete(like);
        return result;
    }
}
