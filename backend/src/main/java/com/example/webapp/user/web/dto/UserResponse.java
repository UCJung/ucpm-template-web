package com.example.webapp.user.web.dto;

import com.example.webapp.user.domain.User;

/**
 * 사용자 응답 DTO — 비밀번호를 제외하고 노출한다.
 */
public record UserResponse(Long id, String username, String displayName, String role, String status) {

	public static UserResponse from(User user) {
		return new UserResponse(
				user.getId(),
				user.getUsername(),
				user.getDisplayName(),
				user.getRole().name(),
				user.getStatus().name());
	}

}
