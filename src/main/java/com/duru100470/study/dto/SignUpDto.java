package com.duru100470.study.dto;

import java.util.ArrayList;
import java.util.List;

import com.duru100470.study.entity.Member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignUpDto {
    @NotBlank
    @Email(message = "잘못된 이메일 형식입니다.")
    private String username;
    @NotBlank
    @Pattern(message = "잘못된 비밀번호 형식입니다."
        , regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&])[A-Za-z[0-9]$@$!%*#?&]{8,15}")
    private String password;
    @NotBlank
    private String nickname;
    private List<String> roles = new ArrayList<>();

    public Member toEntity(String encodedPassword, List<String> roles) {
        return Member.builder()
            .username(username)
            .password(encodedPassword)
            .nickname(nickname)
            .roles(roles)
            .build();
    }
}
