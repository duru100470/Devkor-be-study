package com.duru100470.study.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duru100470.study.dto.JwtToken;
import com.duru100470.study.dto.MemberDto;
import com.duru100470.study.dto.SignUpDto;
import com.duru100470.study.entity.Member;
import com.duru100470.study.error.ApiException;
import com.duru100470.study.error.ExceptionEnum;
import com.duru100470.study.repository.MemberRepository;
import com.duru100470.study.security.JwtTokenProvider;
import com.duru100470.study.security.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public JwtToken signIn(String username, String password) {
        // Authentication 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = 
            new UsernamePasswordAuthenticationToken(username, password);
        
        // 실제 검증
        Authentication authentication =
            authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기반으로 jwt 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        return jwtToken;
    }

    @Transactional
    public MemberDto signUp(SignUpDto signUpDto) {
        if (memberRepository.existsByUsername(signUpDto.getUsername())) {
            throw new ApiException(ExceptionEnum.DUPLICATE_DATA_EXCEPTION, "이미 사용 중인 사용자 이름입니다.");
        }

        // Password 암호화
        String encodedPassword = passwordEncoder.encode(signUpDto.getPassword());
        List<String> roles = new ArrayList<>();
        roles.add("USER");
        return MemberDto.toDto(memberRepository.save(signUpDto.toEntity(encodedPassword, roles)));
    }

    @Transactional
    public MemberDto withdraw(String executer, Long userId) {

        if (!memberRepository.existsById(userId)) {
            throw new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다.");
        }

        Member member = memberRepository.findById(userId)
            .orElseThrow(() -> new ApiException(ExceptionEnum.NOT_FOUND_EXCEPTION, "사용자가 존재하지 않습니다."));

        if (!member.getUsername().equals(executer)) {
            throw new ApiException(ExceptionEnum.FORBIDDEN_EXCEPTION, "권한이 없습니다.");
        }

        memberRepository.deleteById(userId);
        return MemberDto.toDto(member);
    }
}
