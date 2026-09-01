import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, Link } from 'react-router-dom';

import { login, fetchMe } from './authApi';
import { loginSchema, type LoginFormValues } from './authSchema';
import { useSessionStore } from '@/store/useSessionStore';
import { extractApiErrorMessage } from '@/lib/apiClient';

/** 로그인 화면 — 다크 로그인 카드(components.css .login-*). 성공 시 토큰+내정보를 세션에 반영. */
export function LoginPage() {
  const navigate = useNavigate();
  const { setTokens, setMe } = useSessionStore();
  const [serverError, setServerError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormValues>({ resolver: zodResolver(loginSchema) });

  const onSubmit = handleSubmit(async (values) => {
    setServerError(null);
    try {
      const tokens = await login(values.username, values.password);
      setTokens(tokens.accessToken, tokens.refreshToken);
      setMe(await fetchMe());
      navigate('/', { replace: true });
    } catch (err) {
      setServerError(extractApiErrorMessage(err, '로그인에 실패했습니다.'));
    }
  });

  return (
    <div className="login-screen">
      <div className="login-bg" />
      <form className="login-card" onSubmit={onSubmit}>
        <div className="login-logo">
          <span className="logo-mark">◆</span>
          <span className="logo-text">Webapp</span>
        </div>
        <p className="login-sub">템플릿 로그인</p>

        <div className="login-field">
          <label htmlFor="username">아이디</label>
          <input id="username" autoComplete="username" {...register('username')} />
          {errors.username && <p className="login-error">{errors.username.message}</p>}
        </div>

        <div className="login-field">
          <label htmlFor="password">비밀번호</label>
          <input id="password" type="password" autoComplete="current-password" {...register('password')} />
          {errors.password && <p className="login-error">{errors.password.message}</p>}
        </div>

        {serverError && <p className="login-error">{serverError}</p>}

        <button type="submit" className="login-btn" disabled={isSubmitting}>
          {isSubmitting ? '로그인 중…' : '로그인'}
        </button>

        <p className="login-foot">
          계정이 없으신가요? <Link to="/signup">가입하기</Link>
        </p>
      </form>
    </div>
  );
}
