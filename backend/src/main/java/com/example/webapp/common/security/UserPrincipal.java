package com.example.webapp.common.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * SecurityContext에 담기는 인증 주체 — access 토큰 claims에서 복원한다.
 * {@code role}은 {@code ROLE_} 접두어를 붙여 GrantedAuthority로 노출한다(메서드 보안 hasRole 호환).
 */
public class UserPrincipal {

	private final Long id;
	private final String username;
	private final String role;

	public UserPrincipal(Long id, String username, String role) {
		this.id = id;
		this.username = username;
		this.role = role;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getRole() {
		return role;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + role));
	}

}
