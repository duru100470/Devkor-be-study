package com.duru100470.study.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignInDto {
    @NotBlank
    @Email(message = "잘못된 이메일 형식입니다.")
    private String username;
    @NotBlank
    private String password;
}
