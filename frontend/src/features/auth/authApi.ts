import { apiClient, type ApiResponse } from '@/lib/apiClient';
import type { MeInfo } from '@/store/useSessionStore';

interface TokenPair {
  accessToken: string;
  refreshToken: string;
}

export async function login(username: string, password: string): Promise<TokenPair> {
  const { data } = await apiClient.post<ApiResponse<TokenPair>>('/auth/login', { username, password });
  return unwrap(data);
}

export async function signup(username: string, password: string, displayName: string): Promise<MeInfo> {
  const { data } = await apiClient.post<ApiResponse<MeInfo>>('/auth/signup', {
    username,
    password,
    displayName,
  });
  return unwrap(data);
}

export async function fetchMe(): Promise<MeInfo> {
  const { data } = await apiClient.get<ApiResponse<MeInfo>>('/me');
  return unwrap(data);
}

function unwrap<T>(res: ApiResponse<T>): T {
  if (res.result !== 'success' || res.data == null) {
    throw new Error(res.message ?? '요청에 실패했습니다.');
  }
  return res.data;
}
