import { create } from 'zustand';
import { persist } from 'zustand/middleware';

/** GET /api/me 응답(backend UserResponse)과 동일한 필드 구성. */
export interface MeInfo {
  id: number;
  username: string;
  displayName: string | null;
  role: 'USER' | 'ADMIN';
  status: 'ACTIVE' | 'INACTIVE';
}

interface SessionState {
  /** Access 토큰(수명 1h) — axios 인터셉터가 Authorization 헤더로 첨부. */
  accessToken: string | null;
  /** Refresh 토큰(수명 7d) — 401 시 회전에 사용. */
  refreshToken: string | null;
  /** 내 정보 캐시 — 로그인/부팅 시 GET /api/me로 채움. */
  me: MeInfo | null;
  setTokens: (accessToken: string, refreshToken: string) => void;
  setMe: (me: MeInfo | null) => void;
  clearSession: () => void;
}

/**
 * 세션 상태 — access/refresh 토큰은 메모리 + localStorage(persist) 양쪽에 보관하고,
 * me는 persist에서 제외해 부팅 시 GET /api/me로 재조회한다.
 */
export const useSessionStore = create<SessionState>()(
  persist(
    (set) => ({
      accessToken: null,
      refreshToken: null,
      me: null,
      setTokens: (accessToken, refreshToken) => set({ accessToken, refreshToken }),
      setMe: (me) => set({ me }),
      clearSession: () => set({ accessToken: null, refreshToken: null, me: null }),
    }),
    {
      name: 'webapp-session',
      partialize: (state) => ({ accessToken: state.accessToken, refreshToken: state.refreshToken }),
    },
  ),
);

export function isAuthenticated(): boolean {
  return Boolean(useSessionStore.getState().accessToken);
}
