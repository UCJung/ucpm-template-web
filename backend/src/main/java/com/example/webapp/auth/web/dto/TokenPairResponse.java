package com.example.webapp.auth.web.dto;

/**
 * 로그인·토큰 회전 응답 — access/refresh 토큰 쌍. 프런트 {@code lib/apiClient.ts}의
 * {@code TokenPairResponse}와 동일 구조다.
 */
public record TokenPairResponse(String accessToken, String refreshToken) {
}
