package com.example.webapp.auth.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webapp.auth.service.AuthService;
import com.example.webapp.auth.web.dto.LoginRequest;
import com.example.webapp.auth.web.dto.RefreshTokenRequest;
import com.example.webapp.auth.web.dto.SignupRequest;
import com.example.webapp.auth.web.dto.TokenPairResponse;
import com.example.webapp.common.web.ApiResponse;
import com.example.webapp.user.domain.User;
import com.example.webapp.user.service.UserService;
import com.example.webapp.user.web.dto.UserResponse;

import jakarta.validation.Valid;

/**
 * 인증 엔드포인트(permitAll) — 가입·로그인·토큰 회전.
 * <p>이 템플릿은 HelloWorld 수준으로 가입 즉시 활성 계정을 생성한다. 가입 신청 → 운영자 승인
 * 흐름이 필요하면 SignupRequest 도메인을 추가해 확장한다.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final UserService userService;

	public AuthController(AuthService authService, UserService userService) {
		this.authService = authService;
		this.userService = userService;
	}

	@PostMapping("/signup")
	public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
		User user = userService.register(request.username(), request.password(), request.displayName());
		return ApiResponse.ok(UserResponse.from(user));
	}

	@PostMapping("/login")
	public ApiResponse<TokenPairResponse> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.ok(authService.login(request.username(), request.password()));
	}

	@PostMapping("/refresh")
	public ApiResponse<TokenPairResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
		return ApiResponse.ok(authService.refresh(request.refreshToken()));
	}

}
