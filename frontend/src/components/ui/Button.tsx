import { forwardRef, type ButtonHTMLAttributes } from 'react';

import { cn } from '@/lib/utils';

type Variant = 'primary' | 'line' | 'navy' | 'ghost';
type Size = 'md' | 'sm';

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: Variant;
  size?: Size;
}

const VARIANT_CLASS: Record<Variant, string> = {
  primary: 'btn-primary',
  line: 'btn-line',
  navy: 'btn-navy',
  ghost: 'btn-ghost',
};

/** 공통 버튼 — 디자인 토큰 기반 .btn 스타일(components.css)을 래핑한다. */
export const Button = forwardRef<HTMLButtonElement, ButtonProps>(
  ({ variant = 'primary', size = 'md', className, type = 'button', ...props }, ref) => (
    <button
      ref={ref}
      type={type}
      className={cn('btn', VARIANT_CLASS[variant], size === 'sm' && 'btn-sm', className)}
      {...props}
    />
  ),
);
Button.displayName = 'Button';
