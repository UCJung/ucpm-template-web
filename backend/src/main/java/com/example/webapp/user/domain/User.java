package com.example.webapp.user.domain;

import org.springframework.util.StringUtils;

import com.example.webapp.common.domain.AuditableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * 사용자(User) — 로그인 id({@code username})·비밀번호(BCrypt 해시)·이름({@code displayName}).
 * <p>비밀번호는 항상 해시로 저장하며, API 응답에는 DTO로 변환해 노출한다(비밀번호 미노출).</p>
 */
@Entity
@Table(name = "users", uniqueConstraints = {
		@UniqueConstraint(name = "uk_users_username", columnNames = "username")
})
public class User extends AuditableEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "username", length = 255, nullable = false)
	private String username;

	@Column(name = "password", length = 255, nullable = false)
	private String password;

	@Column(name = "display_name", length = 100)
	private String displayName;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", length = 20, nullable = false)
	private UserRole role = UserRole.USER;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", length = 20, nullable = false)
	private UserStatus status = UserStatus.ACTIVE;

	protected User() {
	}

	public User(String username, String password, String displayName) {
		this.username = username;
		this.password = password;
		this.displayName = displayName;
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public UserStatus getStatus() {
		return status;
	}

	public void setStatus(UserStatus status) {
		this.status = status;
	}

	/** 화면 표시용 이름 — displayName이 있으면 그것을, 없으면 username을 반환한다. */
	public String getDisplayLabel() {
		return StringUtils.hasText(displayName) ? displayName : username;
	}

}
