import { z } from 'zod';

/** 로그인 폼 — 아이디·비밀번호 필수. */
export const loginSchema = z.object({
  username: z.string().min(1, '아이디를 입력하세요.'),
  password: z.string().min(1, '비밀번호를 입력하세요.'),
});
export type LoginFormValues = z.infer<typeof loginSchema>;

/**
 * 가입 폼 — 아이디(3~50)·비밀번호(8자 이상)·이름·비밀번호 확인.
 * 규칙을 backend SignupRequest(@Size)와 맞춰 프런트/백엔드 검증이 어긋나지 않게 한다.
 * 비밀번호 확인은 입력 오타 방지용이며 서버로 전송하지 않는다.
 */
export const signupSchema = z
  .object({
    username: z.string().min(3, '아이디는 3자 이상입니다.').max(50, '아이디는 50자 이하입니다.'),
    password: z.string().min(8, '비밀번호는 8자 이상입니다.').max(100),
    passwordConfirm: z.string().min(1, '비밀번호 확인을 입력하세요.'),
    displayName: z.string().min(1, '이름을 입력하세요.').max(100),
  })
  .refine((v) => v.password === v.passwordConfirm, {
    message: '비밀번호와 비밀번호 확인이 일치하지 않습니다.',
    path: ['passwordConfirm'],
  });
export type SignupFormValues = z.infer<typeof signupSchema>;
