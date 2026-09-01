package com.example.webapp.auth.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.webapp.auth.web.dto.TokenPairResponse;
import com.example.webapp.common.security.JwtTokenProvider;
import com.example.webapp.common.web.ApiException;
import com.example.webapp.user.domain.User;
import com.example.webapp.user.domain.UserStatus;
import com.example.webapp.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;

/**
 * 인증 — 로그인(비밀번호 검증 후 토큰 쌍 발급)·토큰 회전(refresh 검증 후 재발급).
 * refresh는 stateless(서명 + type 클레임) 검증이라 서버 저장소가 없다(HelloWorld 수준).
 */
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Transactional(readOnly = true)
	public TokenPairResponse login(String username, String rawPassword) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
						"아이디 또는 비밀번호가 올바르지 않습니다."));
		if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_CREDENTIALS",
					"아이디 또는 비밀번호가 올바르지 않습니다.");
		}
		if (user.getStatus() != UserStatus.ACTIVE) {
			throw new ApiException(HttpStatus.FORBIDDEN, "ACCOUNT_INACTIVE", "비활성화된 계정입니다.");
		}
		return issue(user);
	}

	@Transactional(readOnly = true)
	public TokenPairResponse refresh(String refreshToken) {
		Claims claims;
		try {
			claims = jwtTokenProvider.parseClaims(refreshToken);
		} catch (JwtException | IllegalArgumentException e) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "유효하지 않은 토큰입니다.");
		}
		if (!jwtTokenProvider.isRefreshToken(claims)) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "refresh 토큰이 아닙니다.");
		}
		User user = userRepository.findById(Long.valueOf(claims.getSubject()))
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "INVALID_TOKEN", "사용자를 찾을 수 없습니다."));
		return issue(user);
	}

	private TokenPairResponse issue(User user) {
		return new TokenPairResponse(
				jwtTokenProvider.createAccessToken(user),
				jwtTokenProvider.createRefreshToken(user));
	}

}
