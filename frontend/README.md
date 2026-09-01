# frontend

React 18 + TypeScript + Vite SPA 스캐폴딩. 디자인 토큰(CSS 변수)·공통 UI 프리미티브·API 클라이언트·
세션 스토어·로그인/가입/홈을 HelloWorld 수준으로 담는다. 표준 규약(토큰 기반 스타일·`apiClient` refresh 회전·
Zustand 세션)을 따른다.

## 실행

```bash
npm install
npm run dev                 # http://localhost:5173, /api → :8080 프록시(vite.config.ts)
```

백엔드(`../backend`, :8080)를 함께 띄운 뒤 `admin` / `admin1234`로 로그인한다.

## 빌드·린트

```bash
npm run build               # tsc + vite build
npm run lint
npm run preview             # 빌드 결과 미리보기
```

## UI 공통 요소

| 영역 | 파일 |
|---|---|
| 디자인 토큰(색·간격·메트릭) | `src/styles/tokens.css` (+ `tailwind.config.ts`가 유틸리티로 매핑) |
| 공통 컴포넌트 스타일 | `src/styles/components.css` (로그인·앱셸·버튼·폼·카드·모달·토스트·배지) |
| UI 프리미티브 | `src/components/ui/` — Button · FormField · Card · Badge · Modal · Toast |
| 앱 셸 | `src/components/shell/AppLayout.tsx` |

> 새 색·간격은 지어내지 말고 `tokens.css`에 토큰으로 추가한 뒤 참조한다.

## 구조

```
lib/        apiClient(ApiResponse·401 refresh 회전) · queryClient · utils(cn)
store/      useSessionStore(토큰 persist) · useToastStore
features/   auth(LoginPage · SignupPage · authApi · authSchema) · home(HomePage)
routes/     AppRoutes(인증 가드 + 라우팅)
```
