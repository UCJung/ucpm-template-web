# backend

Spring Boot 3 (Java 17) REST 백엔드 스캐폴딩. 공통요소(응답 래퍼·예외 처리·JWT 보안·감사필드)와
사용자 관리·로그인(id/비밀번호/이름)을 HelloWorld 수준으로 담는다. 표준 규약(패키지 계층·`ApiResponse`/`ApiError`·
JWT 필터)을 따르며, 패키지는 `com.example.webapp`이다. 공통 응답은 `result`·`code`·`data`/`rows`·`message` 래퍼를 사용한다.

## 실행

기본 프로파일은 **local(H2 인메모리)** 로 docker 없이 즉시 실행된다.

```bash
./gradlew bootRun            # http://localhost:8080, 기본 프로파일 local
```

PostgreSQL로 실행하려면 인프라를 띄우고 `postgres` 프로파일로 부팅한다(Flyway가 스키마 소유).

```bash
docker compose -f docker-compose-local.yml up -d
SPRING_PROFILES_ACTIVE=postgres ./gradlew bootRun
```

## 빌드·테스트

```bash
./gradlew build             # 컴파일 + 테스트
./gradlew test              # 스모크 테스트(컨텍스트 로딩)
./gradlew test --tests com.example.webapp.WebappApplicationTests   # 단일 테스트
```

## 데모 계정

첫 기동 시 사용자가 없으면 `admin` / `admin1234`(ADMIN)를 시드한다(`DataInitializer`).

## 엔드포인트

| 메서드·경로 | 인증 | 설명 |
|---|---|---|
| `GET /health` | permitAll | 헬스체크 |
| `POST /api/auth/signup` | permitAll | 가입(즉시 활성 계정 생성) |
| `POST /api/auth/login` | permitAll | 로그인 → access/refresh 토큰 |
| `POST /api/auth/refresh` | permitAll | refresh 토큰으로 재발급 |
| `GET /api/me` | Bearer | 내 계정 조회 |

## 구조

```
common/config    SecurityConfig · JpaAuditingConfig · DataInitializer
common/domain    AuditableEntity(생성·수정 일시)
common/security  JwtTokenProvider · JwtAuthFilter · 401/403 핸들러 · UserPrincipal
common/web       ApiResponse · ApiException · GlobalExceptionHandler · HealthController
user             User(도메인) · UserRepository · UserService · UserController
auth             AuthService · AuthController · Login/Signup/Refresh/TokenPair DTO
```

## 단순화 범위(HelloWorld)

- 감사필드는 생성·수정 **일시만**(생성자·수정자 User FK 제외).
- refresh 토큰은 **stateless**(서명 + type 클레임) — Redis 저장·회전 무효화 없음.
- X-API-KEY 병행 인증·레이트리밋·2축(프로젝트 멤버십) 인가·QueryDSL·POI 제외.
- 가입은 즉시 활성(운영자 승인 흐름 없음).
- 위 항목은 필요 시 확장 지점으로 추가한다.
