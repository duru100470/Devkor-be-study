package com.duru100470.study;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;

import com.duru100470.study.dto.JwtToken;
import com.duru100470.study.dto.MemberDto;
import com.duru100470.study.dto.SignInDto;
import com.duru100470.study.dto.SignUpDto;
import com.duru100470.study.service.MemberService;
import com.duru100470.study.util.DatabaseCleanUp;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class MemberControllerTest {
    @Autowired
    DatabaseCleanUp databaseCleanUp;
    @Autowired
    MemberService memberService;
    @Autowired
    TestRestTemplate testRestTemplate;
    @LocalServerPort
    int randomServerPort;

    private SignUpDto signUpDto;

    @BeforeEach
    void beforeEach() {
        signUpDto = SignUpDto.builder()
            .username("member@study.com")
            .password("secret1234*")
            .nickname("nickname")
            .build();
    }

    @AfterEach
    void afterEach() {
        databaseCleanUp.truncateAllEntity();
    }

    @Test
    public void signUpTest() {
        String url = "http://localhost:" + randomServerPort + "/users/sign-up";
        ResponseEntity<MemberDto> responseEntity = testRestTemplate.postForEntity(url, signUpDto, MemberDto.class);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());

        MemberDto savedMemberDto = responseEntity.getBody();

        assertEquals(signUpDto.getUsername(), savedMemberDto.getUsername());
        assertEquals(signUpDto.getNickname(), savedMemberDto.getNickname());
    }

    @Test
    public void signInTest() {
        memberService.signUp(signUpDto);

        SignInDto signInDto = SignInDto.builder()
                .username("member@study.com")
                .password("secret1234*").build();

        // 로그인 요청
        JwtToken jwtToken = memberService.signIn(signInDto.getUsername(), signInDto.getPassword());

        // HttpHeaders 객체 생성 및 토큰 추가
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(jwtToken.getAccessToken());
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        log.info("httpHeaders = {}", httpHeaders);

        // API 요청 설정
        String url = "http://localhost:" + randomServerPort + "/users/test";
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(httpHeaders), String.class);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(signInDto.getUsername(), responseEntity.getBody());
    }
}
