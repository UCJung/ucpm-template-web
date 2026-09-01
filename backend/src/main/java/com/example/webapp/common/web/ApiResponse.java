package com.example.webapp.common.web;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 공통 API 응답 래퍼. 응답 형태 4종을 하나의 구조로 표현한다(null 필드는 직렬화에서 제외).
 * <ul>
 *   <li>(a) 처리 결과만: {@code {result, code}}</li>
 *   <li>(b) 단건 데이터: {@code {result, code, data}}</li>
 *   <li>(c) 목록(페이징 없음): {@code {result, code, count, rows}}</li>
 *   <li>(d) 목록(페이징): {@code {result, code, data:{page,totalCount,totalPage,rows}}}</li>
 *   <li>(e) 실패: {@code {result, code, message}}</li>
 * </ul>
 * 성공 시 {@code code}는 {@code "0"}, 실패 시 에러 코드를 담는다.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(String result, String code, T data, Integer count, List<?> rows, String message) {

	private static final String SUCCESS = "success";
	private static final String FAIL = "fail";
	private static final String CODE_OK = "0";

	/** (a) 처리 결과만 반환. */
	public static ApiResponse<Void> ok() {
		return new ApiResponse<>(SUCCESS, CODE_OK, null, null, null, null);
	}

	/** (b) 단건 데이터 반환. */
	public static <T> ApiResponse<T> ok(T data) {
		return new ApiResponse<>(SUCCESS, CODE_OK, data, null, null, null);
	}

	/** (c) 목록 데이터 반환 (페이징 없음). */
	public static ApiResponse<Void> list(List<?> rows) {
		return new ApiResponse<>(SUCCESS, CODE_OK, null, rows.size(), rows, null);
	}

	/** (d) 목록 데이터 반환 (페이징) — {@code pageData}에 page·totalCount·totalPage·rows를 담는다. */
	public static <T> ApiResponse<T> paged(T pageData) {
		return new ApiResponse<>(SUCCESS, CODE_OK, pageData, null, null, null);
	}

	/** (e) 실패. */
	public static ApiResponse<Void> fail(String code, String message) {
		return new ApiResponse<>(FAIL, code, null, null, null, message);
	}

}
