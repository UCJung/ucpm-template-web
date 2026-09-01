package com.example.webapp.common.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 설정(app.jwt.*) — access 1h / refresh 7d, HS256 서명키(32바이트 이상).
 */
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {

	private Duration accessTokenValidity = Duration.ofHours(1);
	private Duration refreshTokenValidity = Duration.ofDays(7);
	private String secret;

	public Duration getAccessTokenValidity() {
		return accessTokenValidity;
	}

	public void setAccessTokenValidity(Duration accessTokenValidity) {
		this.accessTokenValidity = accessTokenValidity;
	}

	public Duration getRefreshTokenValidity() {
		return refreshTokenValidity;
	}

	public void setRefreshTokenValidity(Duration refreshTokenValidity) {
		this.refreshTokenValidity = refreshTokenValidity;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

}
