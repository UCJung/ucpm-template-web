import type { Config } from 'tailwindcss';
import tailwindcssAnimate from 'tailwindcss-animate';

// 색·간격은 CSS 변수(styles/tokens.css)를 단일 정본으로 참조한다. 새 색·간격을 여기서 지어내지 말 것.
export default {
  darkMode: ['class'],
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        orange: {
          DEFAULT: 'var(--orange)',
          d: 'var(--orange-d)',
          bg: 'var(--orange-bg)',
          bd: 'var(--orange-bd)',
        },
        dark: {
          DEFAULT: 'var(--dark)',
          2: 'var(--dark-2)',
          3: 'var(--dark-3)',
          line: 'var(--dark-line)',
        },
        navy: {
          DEFAULT: 'var(--navy)',
          d: 'var(--navy-d)',
          bg: 'var(--navy-bg)',
        },
        bg: 'var(--bg)',
        card: {
          DEFAULT: 'var(--card)',
          bd: 'var(--card-bd)',
        },
        'row-hover': 'var(--row-hover)',
        'row-sel': 'var(--row-sel)',
        t1: 'var(--t1)',
        t2: 'var(--t2)',
        t3: 'var(--t3)',
        t4: 'var(--t4)',
        ok: { DEFAULT: 'var(--ok)', bg: 'var(--ok-bg)' },
        warn: { DEFAULT: 'var(--warn)', bg: 'var(--warn-bg)' },
        danger: { DEFAULT: 'var(--danger)', bg: 'var(--danger-bg)' },
        info: { DEFAULT: 'var(--info)', bg: 'var(--info-bg)' },
      },
      borderRadius: {
        DEFAULT: 'var(--radius)',
      },
      boxShadow: {
        card: 'var(--shadow-card)',
      },
      fontFamily: {
        sans: ['Pretendard Variable', 'Pretendard', '-apple-system', 'sans-serif'],
      },
      spacing: {
        'gnb-h': 'var(--gnb-h)',
        'lnb-w': 'var(--lnb-w)',
      },
    },
  },
  plugins: [tailwindcssAnimate],
} satisfies Config;
