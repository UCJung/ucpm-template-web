# DB 스키마 마이그레이션 가이드 (Flyway)

| 항목 | 내용 |
|---|---|
| 설명 | 백엔드 DB 스키마 변경을 Flyway로 작성·검증·적용하는 표준 규약 |
| 생성일 | 2026-09-01 |
| 수정일 | 2026-09-01 |
| 버전 | 0.1.0 |

스키마 소유권은 Flyway가 단독으로 가지며 `V{n}__*.sql`이 유일한 변경 경로임. `postgres` 프로파일의 `ddl-auto: validate`가 엔티티와 실제 스키마의 드리프트를 기동 시점에 검증함.

---

## 목차

| 번호 | 제목 | 설명 |
|---|---|---|
| 1 | 소유권 원칙 | Flyway 단독 소유·프로파일별 동작 |
| 2 | 변경 절차 | 엔티티·마이그레이션 동반 커밋 |
| 3 | V{n} 작성 규약 | 파일명·채번·검증 |
| 4 | 불변 정책 | 적용된 마이그레이션 고정 |
| 5 | baseline 동작 | 빈 DB·기존 DB 처리 |
| 6 | repair·clean 제한 | 복구·삭제 명령 제한 |
| 7 | 필수 금지 | 금지 항목 |

---

## 1. 소유권 원칙

- 스키마 소유권은 Flyway 단독임. `V{n}__*.sql`이 테이블·컬럼·인덱스·PK/FK/CHECK 등 구조를 바꾸는 유일한 경로임.
- 위치는 `backend/src/main/resources/db/migration/`임.
- 프로파일별 동작:

| 프로파일 | 스키마 소유 | ddl-auto |
|---|---|---|
| `local` (H2) | JPA가 엔티티로부터 생성 | `create-drop` (Flyway 비활성) |
| `postgres` | Flyway (`V{n}__*.sql`) | `validate` (드리프트 가드) |

- 애플리케이션 기동 코드(`ApplicationRunner`·`@PostConstruct`·`CommandLineRunner`)는 DDL을 실행하지 않음.

---

## 2. 변경 절차

- JPA 엔티티(컬럼·테이블 추가/변경) 수정과 대응 `V{n}`을 같은 커밋 또는 같은 PR에 포함함.
- 대응 `V{n}`이 없으면 `postgres` 프로파일 기동 시 `validate`가 엔티티-스키마 불일치를 검증 예외로 차단함(드리프트 가드).
- 예시 — `User.java`에 컬럼을 추가하는 변경과 `V2__add_user_locale_column.sql`을 한 PR로 묶음.

---

## 3. V{n} 작성 규약

### 3.1 파일명

- 형식은 `V{n}__{snake_case}.sql`임. 버전과 설명은 밑줄 2개(`__`), 설명부 단어 사이는 밑줄 1개(`_`)로 구분함.
- 설명부는 변경 내용을 이름만으로 파악할 수 있게 작성함.
- 좋은 예: `V3__add_code_group_table.sql`.

### 3.2 번호 채번

- 번호는 `db/migration/`의 현재 최대 버전 + 1로 정함.
- 작업 시작 시 디렉터리를 확인해 번호를 예약하고, PR 단계에서 병렬 작업과의 충돌을 재확인함.
- 충돌 시 아직 병합·적용되지 않은 쪽이 번호를 다음 값으로 올려 재작성함. 이미 적용된 파일은 유지함.

### 3.3 제약·enum 동반

- CHECK 제약이 특정 값 집합을 고정하는 경우, 허용 값(예: enum 상수)을 추가할 때 제약 갱신용 `V{n}`을 함께 작성함.
- 누락 시 신규 값을 가진 행 INSERT/UPDATE가 `violates check constraint` 예외로 실패함.

### 3.4 재실행 안전성

- `DROP`은 `IF EXISTS`를, `UPDATE`는 조건부 `WHERE`를 사용해 대상이 0건이어도 안전하게 작성함.
- 데이터 변환은 실패가 기동을 막는 점을 고려해 대상 행을 조건으로 한정함.

### 3.5 적용 검증

작성 후 아래 순서로 수행함.

1. 임시 DB를 생성함.
2. `--spring.datasource.url`을 명시해 `postgres` 프로파일로 부팅함.

```bash
./gradlew bootRun --args='--spring.profiles.active=postgres --spring.datasource.url=jdbc:postgresql://localhost:5432/<임시DB>'
```

3. `flyway_schema_history`에서 신규 `V{n}` 행의 `success=true`, `type=SQL`을 확인함.
4. 연속 2회 기동해 재실행 시에도 동일 결과가 나오는지(멱등) 확인함.
5. 검증 후 임시 DB를 삭제함.

---

## 4. 불변 정책

- 이미 적용된 `V{n}` 파일(대상 DB `flyway_schema_history`에 `success=true` 기록)은 유지함. 후속 변경은 새 `V{n+1}` 파일로 추가함.
- 적용된 파일을 수정하면 저장된 checksum과 달라져 `validate-on-migrate`가 기동을 중단함.
- `V1__init.sql`(baseline)은 배포 후 유지하고, 변경은 새 `V{n>=2}`로 처리함.

---

## 5. baseline 동작

| 대상 DB | 동작 |
|---|---|
| 빈 DB | `V1`이 실제로 실행되어 스키마를 생성함 |
| 기존(비어 있지 않은) DB | `V1`을 실행하지 않고 이력에만 스탬프함(`baseline-on-migrate=true` + `baseline-version=1`, 데이터·스키마 무변경) |

- 비어 있지 않은 기존 스키마에 처음 적용할 때는 `baseline-on-migrate=true`로 baseline 스탬프를 1회 수행함.

---

## 6. repair·clean 제한

- `flyway clean`(전체 스키마 삭제)은 `clean-disabled: true`로 차단함.
- `flyway repair`는 승인된 checksum 복구 절차로만 사용함. 실행 사유·대조 diff·대상 DB·실행자·승인자·일시를 기록함.

---

## 7. 필수 금지

- 애플리케이션 기동 코드에서 DDL(`CREATE`/`ALTER`/`DROP` 등)을 실행하는 것 → 금지(스키마 소유권은 Flyway 단독).
- 이미 적용된 `V{n}` 파일을 수정하는 것 → 금지(checksum 불일치로 기동 중단).
- 승인 절차 없이 `flyway repair`를 실행하는 것 → 금지.
- `flyway clean`을 실행하는 것 → 금지(`clean-disabled`로 차단).

---

## 참조 파일

- `backend/src/main/resources/db/migration/V1__init.sql` — 초기 스키마
- `backend/src/main/resources/application-postgres.yml` — Flyway·`validate` 설정
- `backend/README.md` — 백엔드 빌드·실행·프로파일
- `docs/[GUIDE]_AUTHORING_STYLE.md` — 문서 작성요령

---

## 문서 갱신 이력

| 버전 | 수정일 | 주요 변경사항 |
|---|---|---|
| 0.1.0 | 2026-09-01 | 최초 작성 — Flyway 마이그레이션 필수 규약 중심의 표준 가이드 |
