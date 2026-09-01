import axios, { type AxiosError, type InternalAxiosRequestConfig } from 'axios';

import { useSessionStore } from '@/store/useSessionStore';

/**
 * 공통 API 응답 래퍼 — backend ApiResponse와 동일 구조.
 * 성공 시 `result="success"`·`code="0"`, 실패 시 `result="fail"`·`code`(에러코드)·`message`.
 * 단건은 `data`, 목록(페이징 없음)은 `count`+`rows`, 목록(페이징)은 `data`에 페이징 객체를 담는다.
 */
export interface ApiResponse<T> {
  result: 'success' | 'fail';
  code: string;
  data?: T | null;
  count?: number;
  rows?: unknown[];
  message?: string;
}

/** axios 에러에서 backend 실패 응답의 `message`를 꺼낸다. 없으면 fallback. */
export function extractApiErrorMessage(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err)) {
    const body = err.response?.data as ApiResponse<unknown> | undefined;
    if (body?.message) {
      return body.message;
    }
  }
  return err instanceof Error ? err.message : fallback;
}

interface TokenPairResponse {
  accessToken: string;
  refreshToken: string;
}

const REFRESH_URL = '/api/auth/refresh';
const LOGIN_PATH = '/login';

/** REST prefix `/api`, Vite dev proxy가 `/api → http://localhost:8080`으로 전달한다(vite.config.ts). */
export const apiClient = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
});

// 요청 인터셉터 — 세션의 accessToken을 Bearer로 첨부.
apiClient.interceptors.request.use((config) => {
  const { accessToken } = useSessionStore.getState();
  if (accessToken) {
    config.headers.set('Authorization', `Bearer ${accessToken}`);
  }
  return config;
});

type RetryableConfig = InternalAxiosRequestConfig & { _retry?: boolean };

// 401 회전 요청이 동시에 여러 건 발생해도 refresh 호출은 한 번만 수행되도록 공유.
let refreshInFlight: Promise<string | null> | null = null;

async function rotateRefreshToken(): Promise<string | null> {
  const { refreshToken } = useSessionStore.getState();
  if (!refreshToken) {
    return null;
  }
  try {
    // 만료된 Bearer 첨부·재귀 호출을 피하기 위해 apiClient가 아닌 axios 원본을 사용한다.
    const { data } = await axios.post<ApiResponse<TokenPairResponse>>(REFRESH_URL, { refreshToken });
    if (data.result !== 'success' || !data.data) {
      return null;
    }
    useSessionStore.getState().setTokens(data.data.accessToken, data.data.refreshToken);
    return data.data.accessToken;
  } catch {
    return null;
  }
}

function redirectToLogin() {
  useSessionStore.getState().clearSession();
  if (typeof window !== 'undefined' && window.location.pathname !== LOGIN_PATH) {
    window.location.assign(LOGIN_PATH);
  }
}

// 응답 인터셉터 — 401 시 refresh 토큰으로 1회 회전 후 원 요청 재시도. 실패하면 로그인 화면으로 이동.
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as RetryableConfig | undefined;
    const status = error.response?.status;
    const isRefreshCall = originalRequest?.url?.includes('/auth/refresh');

    if (status !== 401 || !originalRequest || originalRequest._retry || isRefreshCall) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;
    refreshInFlight = refreshInFlight ?? rotateRefreshToken();
    const newAccessToken = await refreshInFlight;
    refreshInFlight = null;

    if (!newAccessToken) {
      redirectToLogin();
      return Promise.reject(error);
    }

    originalRequest.headers.set('Authorization', `Bearer ${newAccessToken}`);
    return apiClient(originalRequest);
  },
);
