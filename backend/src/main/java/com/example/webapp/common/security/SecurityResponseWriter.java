package com.example.webapp.common.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import com.example.webapp.common.web.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 필터 체인 레벨(401/403) 응답을 컨트롤러 예외 처리와 동일한 {@link ApiResponse}
 * 형태로 직렬화하는 공통 지점.
 */
public final class SecurityResponseWriter {

	private SecurityResponseWriter() {
	}

	public static void write(HttpServletResponse response, HttpServletRequest request, ObjectMapper objectMapper,
			HttpStatus status, String code, String message) throws IOException {
		response.setStatus(status.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		objectMapper.writeValue(response.getWriter(), ApiResponse.fail(code, message));
	}

}
