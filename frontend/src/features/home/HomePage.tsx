import { useQuery } from '@tanstack/react-query';

import { fetchMe } from '@/features/auth/authApi';
import { useSessionStore } from '@/store/useSessionStore';
import { Card } from '@/components/ui/Card';
import { Badge } from '@/components/ui/Badge';
import { Button } from '@/components/ui/Button';
import { useToastStore } from '@/store/useToastStore';

/** HelloWorld 홈 — GET /api/me로 내 정보를 조회해 공통 UI(Card/Badge/Button)로 표시한다. */
export function HomePage() {
  const setMe = useSessionStore((s) => s.setMe);
  const pushToast = useToastStore((s) => s.push);
  const { data: me, isLoading } = useQuery({
    queryKey: ['me'],
    queryFn: async () => {
      const info = await fetchMe();
      setMe(info);
      return info;
    },
  });

  return (
    <>
      <div className="page-head">
        <div>
          <div className="ph-title">Hello, {me?.displayName ?? '방문자'}</div>
          <div className="ph-sub">백엔드 + 프런트엔드 공통요소 스캐폴딩 템플릿</div>
        </div>
        <div className="ph-actions">
          <Button size="sm" variant="line" onClick={() => pushToast('공통 토스트 예시입니다.', 'info')}>
            토스트 테스트
          </Button>
        </div>
      </div>

      <Card title="내 계정 (GET /api/me)" style={{ maxWidth: 480 }}>
        {isLoading || !me ? (
          <p className="empty">불러오는 중…</p>
        ) : (
          <div className="info-list">
            <div className="info-row">
              <span className="k">아이디</span>
              <span className="v">{me.username}</span>
            </div>
            <div className="info-row">
              <span className="k">이름</span>
              <span className="v">{me.displayName ?? '-'}</span>
            </div>
            <div className="info-row">
              <span className="k">역할</span>
              <span className="v">
                <Badge tone={me.role === 'ADMIN' ? 'orange' : 'info'}>{me.role}</Badge>
              </span>
            </div>
            <div className="info-row">
              <span className="k">상태</span>
              <span className="v">
                <Badge tone={me.status === 'ACTIVE' ? 'ok' : 'gray'}>{me.status}</Badge>
              </span>
            </div>
          </div>
        )}
      </Card>
    </>
  );
}
