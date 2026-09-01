package com.example.webapp.common.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.example.webapp.user.domain.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * JWT 발급·검증 — access/refresh 토큰을 서명(HS256)으로 생성하고 파싱한다.
 * refresh 토큰은 Redis 등 서버 저장 없이 서명 + {@code type} 클레임만으로 검증하는 stateless 방식이다
 * (HelloWorld 수준). 로테이션 무효화가 필요하면 저장소 기반 검증으로 확장한다.
 */
@Component
public class JwtTokenProvider {

	private static final String CLAIM_TYPE = "type";
	private static final String CLAIM_USERNAME = "username";
	private static final String CLAIM_ROLE = "role";
	private static final String TYPE_ACCESS = "access";
	private static final String TYPE_REFRESH = "refresh";

	private final SecretKey key;
	private final JwtProperties properties;

	public JwtTokenProvider(JwtProperties properties) {
		this.properties = properties;
		this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(User user) {
		return build(user, TYPE_ACCESS, properties.getAccessTokenValidity().toMillis());
	}

	public String createRefreshToken(User user) {
		return build(user, TYPE_REFRESH, properties.getRefreshTokenValidity().toMillis());
	}

	private String build(User user, String type, long validityMillis) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(String.valueOf(user.getId()))
				.claim(CLAIM_TYPE, type)
				.claim(CLAIM_USERNAME, user.getUsername())
				.claim(CLAIM_ROLE, user.getRole().name())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plusMillis(validityMillis)))
				.signWith(key)
				.compact();
	}

	public Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(key)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean isAccessToken(Claims claims) {
		return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
	}

	public boolean isRefreshToken(Claims claims) {
		return TYPE_REFRESH.equals(claims.get(CLAIM_TYPE, String.class));
	}

	public UserPrincipal toPrincipal(Claims claims) {
		return new UserPrincipal(
				Long.valueOf(claims.getSubject()),
				claims.get(CLAIM_USERNAME, String.class),
				claims.get(CLAIM_ROLE, String.class));
	}

}
