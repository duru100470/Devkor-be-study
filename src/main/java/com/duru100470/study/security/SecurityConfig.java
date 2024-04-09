package com.duru100470.study.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(basic ->basic.disable())
                .csrf(csrf ->csrf.disable())

                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(management ->management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(requests ->requests
                    // 해당 API에 대해서는 모든 요청을 허가
                    .requestMatchers("/test/**").permitAll()
                    .requestMatchers("/docs/**").permitAll()
                    .requestMatchers("/users/sign-in").permitAll()
                    .requestMatchers("/users/sign-up").permitAll()
                    // USER 권한이 있어야 요청할 수 있음
                    .requestMatchers("/users/test").hasRole("USER")
                    // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                    .anyRequest().authenticated())

                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
