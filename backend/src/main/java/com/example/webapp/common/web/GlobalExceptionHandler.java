package com.example.webapp.common.web;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 공통 예외 처리 — 모든 컨트롤러의 예외를 {@link ApiResponse} 표준 실패 형태로 변환한다.
 * 필터 체인 레벨(토큰 부재 등)의 401/403은 SecurityConfig에 등록된 JwtAuthenticationEntryPoint/
 * JwtAccessDeniedHandler가 처리하고, 여기서는 컨트롤러/메서드 보안 내부에서 발생하는 예외를 처리한다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex, HttpServletRequest request) {
		if (ex.getStatus().is5xxServerError()) {
			log.warn("ApiException {} ({}) at {}: {}", ex.getCode(), ex.getStatus().value(),
					request.getRequestURI(), ex.getMessage(), ex);
		}
		return build(ex.getStatus(), ex.getCode(), ex.getMessage(), request);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex,
			HttpServletRequest request) {
		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(f -> f.getField() + ": " + f.getDefaultMessage())
				.findFirst()
				.orElse("요청 값이 유효하지 않습니다.");
		return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message, request);
	}

	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(RuntimeException ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
	}

	/**
	 * 요청 바디를 읽을 수 없는 경우(깨진 JSON·잘못된 인코딩 등) — 500이 아닌 400으로 응답하고
	 * 원본 예외 메시지(내부 구조 노출 가능성)는 응답에 포함하지 않는다.
	 */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex,
			HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "요청 본문을 읽을 수 없습니다.", request);
	}

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
		return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex,
			HttpServletRequest request) {
		return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "인증이 필요합니다.", request);
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
		return build(HttpStatus.FORBIDDEN, "FORBIDDEN", "권한이 없습니다.", request);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex, HttpServletRequest request) {
		log.error("Unhandled exception at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "예기치 못한 오류가 발생했습니다.", request);
	}

	private ResponseEntity<ApiResponse<Void>> build(HttpStatus status, String code, String message,
			HttpServletRequest request) {
		return ResponseEntity.status(status).body(ApiResponse.fail(code, message));
	}

}
