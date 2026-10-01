# operation — 운영 자산 템플릿

| 항목 | 내용 |
|---|---|
| 설명 | 신규 프로젝트에 복사해 쓰는 운영 절차·스크립트·시스템 유닛 모음 |
| 생성일 | 2026-10-01 |
| 수정일 | 2026-10-01 |
| 버전 | 0.1.0 |

운영 주제별 하위 폴더로 구성함. 각 폴더는 `[GUIDE]_*.md`(절차 정본) + `scripts/`(실행 스크립트) + `systemd/`(예약 유닛)을 같은 구조로 가짐. 프로젝트 고유값은 `<설명, 예: 값>` 표기로 두었으며 사용 전 전부 치환함.

---

## 목차

| 번호 | 제목 | 설명 |
|---|---|---|
| 1 | 구성 | 폴더 구조와 주제 |
| 2 | 사용 절차 | 신규 프로젝트 적용 순서 |
| 3 | 치환 규칙 | `<...>` 표기 처리 방법 |
| 4 | 확장 | 운영 주제 추가 방법 |

---

## 1. 구성

| 주제 | 경로 | 정본 문서 |
|---|---|---|
| prod 백업 | `operation/backup/` | `[GUIDE]_PROD_BACKUP.md` |
| prod 복원 | `operation/backup/` | `[GUIDE]_PROD_RESTORE.md` |

```text
operation/
├── README.md
└── backup/
    ├── [GUIDE]_PROD_BACKUP.md        # 백업 정책·설치·실행 정본
    ├── [GUIDE]_PROD_RESTORE.md       # 복원 절차 정본
    ├── scripts/
    │   └── prod-backup.sh            # 백업 실행 스크립트
    └── systemd/
        ├── prod-backup.env.example   # 설정 파일 템플릿
        ├── prod-backup.service       # oneshot 실행 유닛
        └── prod-backup.timer         # 일일 예약 유닛
```

---

## 2. 사용 절차

1. 템플릿에서 신규 프로젝트로 `operation/` 전체를 복사함.
2. 적용할 주제의 `[GUIDE]_*.md`를 읽고 "치환 값" 표의 값을 확정함.
3. 스크립트·설정·유닛 파일의 `<...>` 표기를 확정 값으로 치환함.
4. 가이드의 설치 절차를 서버에서 수행함.
5. 가이드의 실행·확인 절차로 1회 수동 검증 후 예약을 활성화함.
6. 프로젝트의 `CLAUDE.md` 참조 문서 인덱스에 적용한 가이드를 등록함.

---

## 3. 치환 규칙

- 치환 대상 표기: `<설명, 예: 값>` — 설명과 예시를 함께 담음.
- 치환 후 잔여 표기 점검: `grep -rn "<.*예:" operation/` 결과가 비어야 함.
- 비밀값(DB 비밀번호·토큰 등)은 치환 대상에 포함하지 않음. 컨테이너 환경변수·`.env.*`(gitignore)에서만 주입함.
- 쉘 스크립트는 LF 개행을 유지함.

---

## 4. 확장

운영 주제를 추가할 때 `operation/<주제>/` 폴더를 만들고 아래 구조를 따름.

| 구성 | 규칙 |
|---|---|
| `[GUIDE]_*.md` | `docs/[GUIDE]_AUTHORING_STYLE.md`의 문체·구조를 따름 |
| `scripts/` | 프로젝트 고유값을 코드에 두지 않고 설정 파일에서 주입함 |
| `systemd/` | 설치 시 `<앱키>-` 접두사를 붙이는 파일명 규칙을 따름 |

- 추가한 주제를 1절 표와 `CLAUDE.md` 인덱스에 등록함.

---

## 참조 파일

- `operation/backup/[GUIDE]_PROD_BACKUP.md` — prod 백업 정본
- `operation/backup/[GUIDE]_PROD_RESTORE.md` — prod 복원 정본
- `docs/[GUIDE]_DEPLOYMENT.md` — 배포 절차 정본
- `docs/[GUIDE]_AUTHORING_STYLE.md` — 문서 작성요령

---

## 문서 갱신 이력

| 버전 | 수정일 | 주요 변경사항 |
|---|---|---|
| 0.1.0 | 2026-10-01 | 최초 작성 — operation 구조 정의와 prod 백업·복원 주제 추가 |
