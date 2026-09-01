import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { useNavigate, Link } from 'react-router-dom';

import { signup } from './authApi';
import { signupSchema, type SignupFormValues } from './authSchema';
import { useToastStore } from '@/store/useToastStore';
import { extractApiErrorMessage } from '@/lib/apiClient';

/** 가입 화면 — 로그인과 동일한 다크 카드. 성공 시 로그인 화면으로 이동한다. */
export function SignupPage() {
  const navigate = useNavigate();
  const pushToast = useToastStore((s) => s.push);
  const [serverError, setServerError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<SignupFormValues>({ resolver: zodResolver(signupSchema) });

  const onSubmit = handleSubmit(async (values) => {
    setServerError(null);
    try {
      await signup(values.username, values.password, values.displayName);
      pushToast('가입이 완료되었습니다. 로그인해 주세요.', 'ok');
      navigate('/login', { replace: true });
    } catch (err) {
      setServerError(extractApiErrorMessage(err, '가입에 실패했습니다.'));
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
        <p className="login-sub">계정 만들기</p>

        <div className="login-field">
          <label htmlFor="username">아이디</label>
          <input id="username" autoComplete="username" {...register('username')} />
          {errors.username && <p className="login-error">{errors.username.message}</p>}
        </div>

        <div className="login-field">
          <label htmlFor="displayName">이름</label>
          <input id="displayName" {...register('displayName')} />
          {errors.displayName && <p className="login-error">{errors.displayName.message}</p>}
        </div>

        <div className="login-field">
          <label htmlFor="password">비밀번호</label>
          <input id="password" type="password" autoComplete="new-password" {...register('password')} />
          {errors.password && <p className="login-error">{errors.password.message}</p>}
        </div>

        <div className="login-field">
          <label htmlFor="passwordConfirm">비밀번호 확인</label>
          <input id="passwordConfirm" type="password" autoComplete="new-password" {...register('passwordConfirm')} />
          {errors.passwordConfirm && <p className="login-error">{errors.passwordConfirm.message}</p>}
        </div>

        {serverError && <p className="login-error">{serverError}</p>}

        <button type="submit" className="login-btn" disabled={isSubmitting}>
          {isSubmitting ? '가입 중…' : '가입하기'}
        </button>

        <p className="login-foot">
          이미 계정이 있으신가요? <Link to="/login">로그인</Link>
        </p>
      </form>
    </div>
  );
}
