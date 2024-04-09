package com.duru100470.study.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duru100470.study.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    boolean existsByUsername(String username);
}
