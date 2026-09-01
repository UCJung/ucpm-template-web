import { CheckCircle2, AlertTriangle, XCircle, Info } from 'lucide-react';

import { useToastStore, type ToastVariant } from '@/store/useToastStore';
import { cn } from '@/lib/utils';

const ICON: Record<ToastVariant, typeof Info> = {
  ok: CheckCircle2,
  warn: AlertTriangle,
  danger: XCircle,
  info: Info,
};

const VARIANT_CLASS: Record<ToastVariant, string> = {
  ok: '',
  warn: 'warn',
  danger: 'danger',
  info: 'info',
};

/** 전역 토스트 뷰포트 — App 루트에 한 번 마운트한다. useToastStore.push로 발행한다. */
export function ToastViewport() {
  const toasts = useToastStore((s) => s.toasts);
  if (toasts.length === 0) {
    return null;
  }
  return (
    <div className="toast-wrap">
      {toasts.map((t) => {
        const Icon = ICON[t.variant];
        return (
          <div key={t.id} className={cn('toast', VARIANT_CLASS[t.variant])}>
            <Icon size={20} />
            <span>{t.message}</span>
          </div>
        );
      })}
    </div>
  );
}
