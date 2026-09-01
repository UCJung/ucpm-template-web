import type { ReactNode } from 'react';

import { cn } from '@/lib/utils';

type Tone = 'ok' | 'warn' | 'danger' | 'info' | 'gray' | 'orange';

/** 공통 배지(.badge) — 상태 색조를 토큰 기반 클래스로 매핑한다. */
export function Badge({ tone = 'gray', children }: { tone?: Tone; children: ReactNode }) {
  return <span className={cn('badge', `badge-${tone}`)}>{children}</span>;
}
