# 백엔드 API 명세 (SPEC)

| 항목 | 내용 |
|---|---|
| 설명 | 백엔드 REST API의 공통 규격과 엔드포인트별 요청·응답 명세 |
| 생성일 | 2026-09-01 |
| 수정일 | 2026-09-01 |
| 버전 | 0.2.0 |

REST prefix는 `/api`이며 모든 응답은 공통 래퍼(`result`·`code`·`data`/`count`·`rows`·`message`)로 감쌈. 보호 엔드포인트는 `Authorization: Bearer {accessToken}` 헤더로 인증함. 이 문서는 현재 구현된 API를 기술하며 신규 API 추가 시 동일 구조로 절을 추가함.

---

## 목차

| 번호 | 제목 | 설명 |
|---|---|---|
| 1 | 공통 규격 | 요청·응답 공통 형식과 필드 |
| 2 | API 목록 | 엔드포인트 요약 |
| 3 | API 상세 | 엔드포인트별 요청·응답 |
| 4 | 에러 코드 | 에러 코드·메시지 일람 |

---

## 1. 공통 규격

### 1.1 Request 공통

```
Header
Content-Type: application/json; charset=utf-8
Authorization: Bearer {accessToken}
```

- `Authorization` 헤더는 보호 엔드포인트에 필요함. `/health`·`/api/auth/**`는 인증 없이 호출함.
- access 토큰 만료(401) 시 `/api/auth/refresh`로 재발급함.

### 1.2 Response 공통

응답 형태는 4종이다.

**(a) 처리 결과만 반환**

```json
{ "result": "success", "code": "0" }
```

**(b) 단건 데이터 반환**

```json
{ "result": "success", "code": "0", "data": { } }
```

**(c) 목록 데이터 반환 (페이징 없음)**

```json
{ "result": "success", "code": "0", "count": 2, "rows": [ ] }
```

**(d) 목록 데이터 반환 (페이징)**

```json
{ "result": "success", "code": "0", "data": { "page": 1, "totalCount": 9, "totalPage": 1, "rows": [ ] } }
```

**(e) 실패**

```json
{ "result": "fail", "code": "1201", "message": "Requested resource does not match." }
```

| 필드명 | 필드타입 | 필드설명 | 비고 |
|---|---|---|---|
| result | String | 요청처리 결과 | success or fail |
| code | String | 결과 코드 | 0 or 에러코드 |
| message | String | 에러 메시지 | 에러 발생시 추가되는 필드 |
| data | Object | 단건 응답 객체 또는 페이징 응답 객체 | (b), (d) 형태 |
| count | Integer | 목록 데이터 개수 | (c) 형태 |
| rows | List\<Object\> | 목록형 데이터 | (c), (d) 형태 |

- 헬스 체크(`/health`)는 공통 래퍼를 사용하지 않고 `{ "status": "UP" }`를 반환한다.
- 목록·페이징((c)·(d)) 형태는 현재 미구현이며 목록 API 도입 시 사용한다(확장 지점).
- 주요 에러 코드: `VALIDATION_ERROR`(400) · `UNAUTHORIZED`(401) · `INVALID_CREDENTIALS`(401) · `INVALID_TOKEN`(401) · `ACCOUNT_INACTIVE`(403) · `FORBIDDEN`(403) · `NOT_FOUND`(404) · `USERNAME_TAKEN`(409) · `INTERNAL_ERROR`(500). 성공 시 `code`는 `0`이다.

---

## 2. API 목록

| # | 그룹 | API명 | Method | URI | 인증 |
|---|---|---|---|---|---|
| 3.1 | 시스템 | 헬스 체크 API | GET | `/health` | 불필요 |
| 3.2 | 인증 | 가입 API | POST | `/api/auth/signup` | 불필요 |
| 3.3 | 인증 | 로그인 API | POST | `/api/auth/login` | 불필요 |
| 3.4 | 인증 | 토큰 회전 API | POST | `/api/auth/refresh` | 불필요 |
| 3.5 | 사용자 | 내 정보 조회 API | GET | `/api/me` | Bearer |

---

## 3. API 상세

### 3.1 헬스 체크 API

#### 개요

- 서버 기동 상태를 확인하는 API. 인증이 필요 없으며 공통 래퍼를 사용하지 않음.

#### Request

```
method : GET
Request URI : http://{API_HOST}/health
```

#### Response data

```json
{ "status": "UP" }
```

#### Response data 설명

| 필드명 | 필드타입 | 필드설명 | 비고 |
|---|---|---|---|
| status | String | 서버 상태 | 정상 시 `UP` |

---

### 3.2 가입 API

#### 개요

- 사용자 계정을 생성하는 API. 생성 즉시 활성(ACTIVE) 상태의 계정을 반환함.

#### Request

```
method : POST
Request URI : http://{API_HOST}/api/auth/signup

Header
Content-Type: application/json; charset=utf-8

Body
{ "username": "alice", "password": "alice1234!", "displayName": "앨리스" }
```

#### Request data 설명

| 필드명 | 필드타입 | 필드설명 | 필수여부 | 비고 |
|---|---|---|---|---|
| username | String | 로그인 아이디 | Y | 3~50자, 중복 불가 |
| password | String | 비밀번호 | Y | 8~100자 |
| displayName | String | 표시 이름 | Y | 1~100자 |

#### Response data

```json
{
  "result": "success",
  "code": "0",
  "data": {
    "id": 3,
    "username": "alice",
    "displayName": "앨리스",
    "role": "USER",
    "status": "ACTIVE"
  }
}
```

#### Response data 설명

| 필드명 | 필드타입 | 필드설명 | 비고 |
|---|---|---|---|
| data | Object | 생성된 사용자 |  |
| ㄴid | Integer | 사용자 ID |  |
| ㄴusername | String | 로그인 아이디 |  |
| ㄴdisplayName | String | 표시 이름 |  |
| ㄴrole | String | 권한 | `USER` or `ADMIN` |
| ㄴstatus | String | 계정 상태 | `ACTIVE` or `INACTIVE` |

- 주요 에러: `VALIDATION_ERROR`(400, 입력 형식 위반) · `USERNAME_TAKEN`(409, 아이디 중복).

---

### 3.3 로그인 API

#### 개요

- 아이디·비밀번호를 검증하고 access·refresh 토큰 쌍을 발급하는 API.

#### Request

```
method : POST
Request URI : http://{API_HOST}/api/auth/login

Header
Content-Type: application/json; charset=utf-8

Body
{ "username": "admin", "password": "admin1234" }
```

#### Request data 설명

| 필드명 | 필드타입 | 필드설명 | 필수여부 | 비고 |
|---|---|---|---|---|
| username | String | 로그인 아이디 | Y |  |
| password | String | 비밀번호 | Y |  |

#### Response data

```json
{
  "result": "success",
  "code": "0",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

#### Response data 설명

| 필드명 | 필드타입 | 필드설명 | 비고 |
|---|---|---|---|
| data | Object | 토큰 쌍 |  |
| ㄴaccessToken | String | 접근 토큰 | 유효기간 1시간, `Authorization: Bearer`에 사용 |
| ㄴrefreshToken | String | 갱신 토큰 | 유효기간 7일, 토큰 회전에 사용 |

- 주요 에러: `INVALID_CREDENTIALS`(401, 아이디·비밀번호 불일치) · `ACCOUNT_INACTIVE`(403, 비활성 계정).

---

### 3.4 토큰 회전 API

#### 개요

- refresh 토큰을 검증하고 새 access·refresh 토큰 쌍을 발급하는 API. access 토큰 만료 시 사용함.

#### Request

```
method : POST
Request URI : http://{API_HOST}/api/auth/refresh

Header
Content-Type: application/json; charset=utf-8

Body
{ "refreshToken": "eyJhbGciOiJIUzUxMiJ9..." }
```

#### Request data 설명

| 필드명 | 필드타입 | 필드설명 | 필수여부 | 비고 |
|---|---|---|---|---|
| refreshToken | String | 갱신 토큰 | Y | 로그인·직전 회전 응답의 값 |

#### Response data

```json
{
  "result": "success",
  "code": "0",
  "data": {
    "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
    "refreshToken": "eyJhbGciOiJIUzUxMiJ9..."
  }
}
```

#### Response data 설명

| 필드명 | 필드타입 | 필드설명 | 비고 |
|---|---|---|---|
| data | Object | 토큰 쌍 |  |
| ㄴaccessToken | String | 접근 토큰 | 유효기간 1시간 |
| ㄴrefreshToken | String | 갱신 토큰 | 유효기간 7일 |

- 주요 에러: `INVALID_TOKEN`(401, refresh 토큰 무효·형식 불일치).

---

### 3.5 내 정보 조회 API

#### 개요

- access 토큰으로 인증한 사용자 본인의 계정 정보를 조회하는 API.

#### Request

```
method : GET
Request URI : http://{API_HOST}/api/me

Header
Content-Type: application/json; charset=utf-8
Authorization: Bearer {accessToken}
```

#### Request data 설명

- 요청 바디 없음. 인증은 `Authorization` 헤더로 수행함.

#### Response data

```json
{
  "result": "success",
  "code": "0",
  "data": {
    "id": 1,
    "username": "admin",
    "displayName": "관리자",
    "role": "ADMIN",
    "status": "ACTIVE"
  }
}
```

#### Response data 설명

| 필드명 | 필드타입 | 필드설명 | 비고 |
|---|---|---|---|
| data | Object | 사용자 |  |
| ㄴid | Integer | 사용자 ID |  |
| ㄴusername | String | 로그인 아이디 |  |
| ㄴdisplayName | String | 표시 이름 |  |
| ㄴrole | String | 권한 | `USER` or `ADMIN` |
| ㄴstatus | String | 계정 상태 | `ACTIVE` or `INACTIVE` |

- 주요 에러: `UNAUTHORIZED`(401, 토큰 부재·무효).

---

## 4. 에러 코드

실패 응답은 `{ "result": "fail", "code": {CodeName}, "message": {Message} }` 형태이다. `Code`는 HTTP 상태 코드, `CodeName`은 응답 `code` 필드 값이다.

| Code | CodeName | Message | Description |
|---|---|---|---|
| 400 | VALIDATION_ERROR | {필드}: {검증 메시지} | 요청 바디 검증 실패(`@Valid`)<br>`{"result":"fail","code":"VALIDATION_ERROR","message":"displayName: 이름을 입력하세요."}` |
| 400 | BAD_REQUEST | 요청 본문을 읽을 수 없습니다. | 잘못된 JSON·타입·인자<br>`{"result":"fail","code":"BAD_REQUEST","message":"요청 본문을 읽을 수 없습니다."}` |
| 401 | UNAUTHORIZED | 인증이 필요합니다. | 토큰 부재·무효(인증 필터)<br>`{"result":"fail","code":"UNAUTHORIZED","message":"인증이 필요합니다."}` |
| 401 | INVALID_CREDENTIALS | 아이디 또는 비밀번호가 올바르지 않습니다. | 로그인 시 아이디·비밀번호 불일치<br>`{"result":"fail","code":"INVALID_CREDENTIALS","message":"아이디 또는 비밀번호가 올바르지 않습니다."}` |
| 401 | INVALID_TOKEN | 유효하지 않은 토큰입니다. | refresh 토큰 무효·형식 불일치<br>`{"result":"fail","code":"INVALID_TOKEN","message":"유효하지 않은 토큰입니다."}` |
| 403 | ACCOUNT_INACTIVE | 비활성화된 계정입니다. | 비활성(INACTIVE) 계정 로그인<br>`{"result":"fail","code":"ACCOUNT_INACTIVE","message":"비활성화된 계정입니다."}` |
| 403 | FORBIDDEN | 권한이 없습니다. | 인가 실패(권한 부족)<br>`{"result":"fail","code":"FORBIDDEN","message":"권한이 없습니다."}` |
| 404 | NOT_FOUND | 요청한 리소스가 없습니다. | 대상 리소스 미존재<br>`{"result":"fail","code":"NOT_FOUND","message":"사용자를 찾을 수 없습니다."}` |
| 409 | USERNAME_TAKEN | 이미 사용 중인 아이디입니다. | 가입 시 아이디 중복<br>`{"result":"fail","code":"USERNAME_TAKEN","message":"이미 사용 중인 아이디입니다."}` |
| 500 | INTERNAL_ERROR | 예기치 못한 오류가 발생했습니다. | 미처리 서버 예외<br>`{"result":"fail","code":"INTERNAL_ERROR","message":"예기치 못한 오류가 발생했습니다."}` |

- `VALIDATION_ERROR`의 `message`는 위반 필드와 메시지를 조합해 동적으로 생성한다.
- `NOT_FOUND`의 `message`는 미존재 리소스에 따라 달라진다.

---

## 참조 파일

- `backend/src/main/java/com/example/webapp/auth/web/AuthController.java` — 인증 엔드포인트
- `backend/src/main/java/com/example/webapp/user/web/UserController.java` — 내 정보 조회
- `backend/src/main/java/com/example/webapp/common/web/ApiResponse.java` · `ApiError.java` — 공통 응답 래퍼
- `backend/README.md` — 엔드포인트 요약·실행

---

## 문서 갱신 이력

| 버전 | 수정일 | 주요 변경사항 |
|---|---|---|
| 0.1.0 | 2026-09-01 | 최초 작성 — 현재 구현 API(헬스·인증·내 정보) 명세 |
| 0.2.0 | 2026-09-01 | 공통 응답 구조(`result`·`code`) 반영, 에러 코드 절 추가 |
