import type { HTMLAttributes, ReactNode } from 'react';

import { cn } from '@/lib/utils';

interface CardProps extends Omit<HTMLAttributes<HTMLDivElement>, 'title'> {
  title?: ReactNode;
  actions?: ReactNode;
}

/** 공통 카드(.card) — 선택적 헤더(title/actions) + 바디. */
export function Card({ title, actions, className, children, ...props }: CardProps) {
  return (
    <div className={cn('card', className)} {...props}>
      {(title || actions) && (
        <div className="card-h">
          <span className="ct">{title}</span>
          {actions}
        </div>
      )}
      <div className="card-b">{children}</div>
    </div>
  );
}
