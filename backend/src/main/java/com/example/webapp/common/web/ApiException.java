package com.example.webapp.common.web;

import org.springframework.http.HttpStatus;

/**
 * 도메인 서비스에서 특정 HTTP 상태·에러코드·메시지를 명시적으로 지정해야 하는 실패를 표현한다
 * (예: 로그인 실패 401/INVALID_CREDENTIALS). {@link GlobalExceptionHandler}가
 * {@link ApiResponse} 표준 실패 형태(result·code·message)로 변환한다.
 */
public class ApiException extends RuntimeException {

	private final HttpStatus status;
	private final String code;

	public ApiException(HttpStatus status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public String getCode() {
		return code;
	}

}
