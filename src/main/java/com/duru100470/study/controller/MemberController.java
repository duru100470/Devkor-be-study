package com.duru100470.study.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.duru100470.study.dto.*;
import com.duru100470.study.security.SecurityUtil;
import com.duru100470.study.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class MemberController {
    private final MemberService memberService;
    private final SecurityUtil securityUtil;

    @PostMapping("/sign-in")
    public JwtToken signIn(@Valid @RequestBody SignInDto signInDto) {
        String username = signInDto.getUsername();
        String password = signInDto.getPassword();

        JwtToken jwtToken = memberService.signIn(username, password);

        log.info("request username = {}, password = {}", username, password);
        log.info("jwtToken accessToken = {}, refreshToken = {}", jwtToken.getAccessToken(), jwtToken.getRefreshToken());

        return jwtToken;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<MemberDto> signUp(@Valid @RequestBody SignUpDto signUpDto) {
        MemberDto saveMemberDto = memberService.signUp(signUpDto);
        return ResponseEntity.ok(saveMemberDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MemberDto> withdraw(@PathVariable("id") Long id) {
        String executer = securityUtil.getCurrentUsername();
        MemberDto memberDto = memberService.withdraw(executer, id);
        return ResponseEntity.ok(memberDto);
    }

    @GetMapping("/test")
    public String doTest() {
        return securityUtil.getCurrentUsername();
    }
}
