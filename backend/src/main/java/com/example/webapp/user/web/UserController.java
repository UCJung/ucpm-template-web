package com.example.webapp.user.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.webapp.common.security.UserPrincipal;
import com.example.webapp.common.web.ApiResponse;
import com.example.webapp.user.service.UserService;
import com.example.webapp.user.web.dto.UserResponse;

/**
 * 인증된 사용자 자신의 정보 조회(GET /api/me). 그 외 사용자 관리 API의 확장 지점이다.
 */
@RestController
@RequestMapping("/api")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/me")
	public ApiResponse<UserResponse> me(@AuthenticationPrincipal UserPrincipal principal) {
		return ApiResponse.ok(UserResponse.from(userService.getById(principal.getId())));
	}

}
