import type { ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { LogOut } from 'lucide-react';

import { useSessionStore } from '@/store/useSessionStore';

/** 앱 셸 — 상단 GNB(로고 + 사용자 + 로그아웃) + 콘텐츠 영역. */
export function AppLayout({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const { me, clearSession } = useSessionStore();

  const onLogout = () => {
    clearSession();
    navigate('/login', { replace: true });
  };

  const label = me?.displayName ?? me?.username ?? '사용자';

  return (
    <div className="app-shell">
      <header className="gnb">
        <div className="gnb-row">
          <span className="gnb-logo">◆ Webapp</span>
          <div className="gnb-util">
            <span className="gnb-user">
              <span className="gnb-avatar">{label.slice(0, 1)}</span>
              {label}
            </span>
            <button type="button" className="gnb-user" onClick={onLogout} aria-label="로그아웃">
              <LogOut size={16} />
            </button>
          </div>
        </div>
      </header>
      <div className="app-body">
        <main className="content">{children}</main>
      </div>
    </div>
  );
}
