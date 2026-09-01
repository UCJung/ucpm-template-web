package com.example.webapp.user.service;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webapp.common.web.ApiException;
import com.example.webapp.user.domain.User;
import com.example.webapp.user.repository.UserRepository;

/**
 * 사용자 관리 — 가입(등록)·조회. 비밀번호는 BCrypt로 해시해 저장한다.
 */
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public User register(String username, String rawPassword, String displayName) {
		if (userRepository.existsByUsername(username)) {
			throw new ApiException(HttpStatus.CONFLICT, "USERNAME_TAKEN", "이미 사용 중인 아이디입니다.");
		}
		User user = new User(username, passwordEncoder.encode(rawPassword), displayName);
		return userRepository.save(user);
	}

	@Transactional(readOnly = true)
	public User getById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
	}

}
