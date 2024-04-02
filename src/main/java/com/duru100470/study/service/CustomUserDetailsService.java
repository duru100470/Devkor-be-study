package com.duru100470.study.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.duru100470.study.entity.Member;
import com.duru100470.study.error.ApiException;
import com.duru100470.study.error.ExceptionEnum;
import com.duru100470.study.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    // private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
            .map(this::createUserDetails)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "해당하는 회원을 찾을 수 없습니다."));
    }

    public UserDetails createUserDetails(Member member) {
        return User.builder()
            .username(member.getUsername())
            .password(member.getPassword())
            .roles(member.getRoles().toArray(new String[0]))
            .build();
    }
}
