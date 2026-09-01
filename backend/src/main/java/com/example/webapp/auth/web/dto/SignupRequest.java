package com.example.webapp.auth.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
		@NotBlank(message = "아이디를 입력하세요.") @Size(min = 3, max = 50, message = "아이디는 3~50자입니다.") String username,
		@NotBlank(message = "비밀번호를 입력하세요.") @Size(min = 8, max = 100, message = "비밀번호는 8자 이상입니다.") String password,
		@NotBlank(message = "이름을 입력하세요.") @Size(max = 100) String displayName) {
}
