# 기술 스택 (SPEC)

| 항목 | 내용 |
|---|---|
| 설명 | 이 템플릿을 클론하는 프로젝트의 표준 기술 스택과 현재 스캐폴딩 반영 범위 정의 |
| 생성일 | 2026-09-01 |
| 수정일 | 2026-09-01 |
| 버전 | 0.1.0 |

백엔드는 Spring Boot 3(Java 17), 프런트엔드는 React 18 + Vite를 표준으로 삼음. 현재 스캐폴딩은 HelloWorld 최소 구성이며, `확장 지점` 항목은 도입 시 활성화함.

---

## 목차

| 번호 | 제목 | 설명 |
|---|---|---|
| 1 | 백엔드 | 백엔드 표준 스택과 반영 여부 |
| 2 | 프런트엔드 | 프런트엔드 표준 스택과 반영 여부 |
| 3 | 스캐폴딩 적용 범위 | 현재 적용·확장 지점 요약 |

---

## 1. 백엔드

| 기술 | 버전 | 용도 | 스캐폴딩 반영 |
|---|---|---|---|
| Java | 17 | 언어·런타임 | 적용 |
| Spring Boot | 3.x | 웹 프레임워크 | 적용 |
| Spring Security + JWT | Access 1h / Refresh 7d | 인증·인가 | 적용(refresh는 stateless, Redis 저장 미적용) |
| JPA (Hibernate) | — | 영속성 | 적용 |
| QueryDSL | 5.x | 타입 안전 동적 쿼리 | 확장 지점 |
| H2 | — | local 프로파일 인메모리 DB | 적용(기본 프로파일 `local`) |
| PostgreSQL | 16 | 운영 DB | 적용(`postgres` 프로파일) |
| Flyway | — | DB 스키마 마이그레이션(`V{n}`) | 적용(`postgres` 프로파일) |
| TimescaleDB | — | 시계열 확장 | 확장 지점 |
| Redis | 7 | 토큰·캐시 저장 | 확장 지점 |
| Apache POI | 5.3.x | 엑셀 입출력 | 확장 지점 |

---

## 2. 프런트엔드

| 기술 | 버전 | 용도 | 스캐폴딩 반영 |
|---|---|---|---|
| React + TypeScript | 18 / 5 | UI | 적용 |
| Vite | 5 | 번들러·개발서버 | 적용 |
| Tailwind CSS | 3 | 스타일(디자인 토큰 매핑) | 적용 |
| class-variance-authority · clsx · tailwind-merge | — | 컴포넌트 변형·클래스 병합 | 적용 |
| shadcn/ui | — | UI 프리미티브 | 확장 지점(현재 프리미티브 자체 구현) |
| Zustand | 4 | 전역 UI·세션 상태 | 적용 |
| TanStack Query | v5 | 서버 상태(목록·상세·mutation) | 적용 |
| React Router | v6 | 라우팅 | 적용 |
| react-hook-form + zod | — | 폼·검증 | 적용 |
| lucide-react | — | 아이콘 | 적용 |
| Recharts | — | 차트 | 확장 지점(차트 필요 시 도입) |

---

## 3. 스캐폴딩 적용 범위

- **적용**: Java 17 · Spring Boot 3 · Security/JWT · JPA · H2(local)·PostgreSQL(postgres)·Flyway · React 18/TS · Vite · Tailwind · Zustand · TanStack Query · React Router · react-hook-form/zod · lucide-react.
- **확장 지점(미적용)**: QueryDSL · Redis · TimescaleDB · Apache POI · shadcn/ui · Recharts. 도입 시 이 문서의 반영 여부를 갱신함.

---

## 참조 파일

- `backend/build.gradle` · `backend/README.md` — 백엔드 의존성·구성 정본
- `frontend/package.json` · `frontend/README.md` — 프런트 의존성·구성 정본
- `CLAUDE.md` — 참조 문서 인덱스
- `docs/[GUIDE]_AUTHORING_STYLE.md` — 문서 작성요령

---

## 문서 갱신 이력

| 버전 | 수정일 | 주요 변경사항 |
|---|---|---|
| 0.1.0 | 2026-09-01 | 최초 작성 — `CLAUDE.md` 「기술 스택」 절 분리, 스캐폴딩 반영 여부 명시 |
