# UCPM-QUICK-GUIDE — 설치 및 사용 지시문 모음

- **설명**: UCPM-PIPELINE 환경 구성(설치·설정) 명령과 단계별 사용 지시문만 추린 실무용 빠른 가이드.
- **예시 기준**: 프로젝트명 `ucpm-case-study`, 작업 경로 `c:\work\ucpm-case-study`. 모든 명령은 **Windows PowerShell**에서 실행. `ucpm-case-study`·`CASESTUDY`는 실제 프로젝트명·키로 교체.
- **문서참조 경로 표기**: 이 가이드가 참조하는 규약·양식 문서 경로는 이 저장소 루트 기준 상대경로 `docs/…`로 표기(클론 후에는 실제 프로젝트 폴더의 동일 경로).

---

## 1. 환경 구성 (설치)

### 1-0. 사전 준비

`git`·GitHub CLI(`gh`)·Node.js 20 이상 설치 + `gh auth login`으로 GitHub 로그인.

```powershell
winget install Git.Git
winget install GitHub.cli
winget install OpenJS.NodeJS
gh auth login
```

### 1-1. Claude 설치

```powershell
# (권장) Windows 네이티브 설치
irm https://claude.ai/install.ps1 | iex
```

```powershell
# (대안) npm 전역 설치 — Node.js 22 이상
npm install -g @anthropic-ai/claude-code
```

```powershell
claude                  # 최초 실행 → 브라우저 로그인 → "Login successful"
claude --version        # 설치·버전 확인
claude doctor           # 설치 상태·설정 진단(선택)
```

- 확인: `claude --version`에 버전 문자열 출력.

### 1-2. 스캐폴딩 (템플릿 클론 → 이력 분리 → 신규 저장소)

```powershell
# 1) 작업 폴더로 이동 후 템플릿 클론
Set-Location c:\work
git clone https://github.com/UCJung/ucpm-template-web ucpm-case-study
Set-Location c:\work\ucpm-case-study

# 2) 템플릿 git 이력 분리 후 신규 이력 시작
Remove-Item -Recurse -Force .git
git init
git add .
git commit -m "chore: scaffold from ucpm-template-web"

# 3) 신규 원격 저장소 생성·연결·푸시
gh repo create ucpm-case-study --private --source=. --remote=origin --push
```

- 확인: `c:\work\ucpm-case-study` 폴더 생성 + GitHub 저장소 생성·푸시 완료.
- 템플릿 기본 문서 목록: 아래 [3. 참조 문서](#3-참조-문서) 참조.

### 1-3. ucpm-agent 설치 + Claude Code 설정

```powershell
# 배포 zip을 푼 폴더에서 전역 설치 (설치 CLI 등록)
npm install -g ./ucjung-ucpm-agent-<version>.tgz

# 프로젝트 폴더로 이동 후 배치 (→ .claude/)
Set-Location c:\work\ucpm-case-study
ucpm-agent-install
```

프로젝트 루트 `CLAUDE.md`에 아래 한 절을 추가(없으면 파일 생성).

```markdown
## OperationGuide
docs/UCPM_PIPELINE_GUIDE.md
```

**Claude Code 설정 변경(중첩 스폰 한도)** — `settings.json`의 `env`에 아래 값 설정(전역 `~/.claude/settings.json` 또는 프로젝트 `.claude/settings.json` 중 택1).

```json
{
  "env": {
    "CLAUDE_CODE_MAX_SUBAGENT_SPAWN_DEPTH": "3",
    "CLAUDE_CODE_MAX_CONCURRENT_SUBAGENTS": "10"
  }
}
```

| 변수 | 값 | 용도 |
|---|---|---|
| `CLAUDE_CODE_MAX_SUBAGENT_SPAWN_DEPTH` | `3` | 중첩 스폰 최대 깊이. 정상 경로는 깊이 2 필요(Main→orchestrator→하위). 기본값(3)으로도 충족되나 명시 고정 |
| `CLAUDE_CODE_MAX_CONCURRENT_SUBAGENTS` | `10` | 동시 실행 서브에이전트 상한(기본 20). TASK DAG 병렬 처리 시 조정 |

- 확인: `.claude\` 아래 `agents`·`references`·`skills` 폴더 생성. 설정은 **새 세션(재시작)**부터 반영.

### 1-4. ucpm-mcp 설치 및 설정

> **선행(웹)** — `UCPM_PROJECT_KEY`·`UCPM_API_KEY`는 UCPM 웹에서 확보.
> - **프로젝트 생성**: 상단바 프로젝트 오버레이 → 「프로젝트 생성 신청」 → 프로젝트명·키(대문자·숫자 4~9자)·설명 → 중복확인 → 운영자 승인(`/project-approvals`).
> - **인증키 생성**: 사용자 메뉴 → 「인증키 관리」(`/apikeys`) → 「인증키 발급」 → 이름·범위(scope)·만료 지정 → 발급 → 원문 키(`mcp_…`)를 **1회 표시 시점**에 복사.

```powershell
# 배포 zip을 푼 폴더에서 전역 설치
npm install -g ./ucjung-ucpm-mcp-server-<version>.tgz
```

프로젝트 루트 `.mcp.json` 작성(서버주소·프로젝트키는 배포자에게 받은 값으로 교체).

```json
{
  "mcpServers": {
    "ucpm-mcp": {
      "command": "ucpm-mcp-server",
      "args": [],
      "env": {
        "UCPM_API_BASE_URL": "http://<서버주소>:<포트>",
        "UCPM_API_KEY": "${UCPM_API_KEY}",
        "UCPM_PROJECT_KEY": "CASESTUDY"
      }
    }
  }
}
```

```powershell
# 인증키 환경변수 주입(영구, 새 터미널부터 적용)
setx UCPM_API_KEY mcp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

- 확인: 새 PowerShell에서 `claude` 실행 → `ucpm_whoami` 호출 시 본인 계정·`serverVersion` 응답.

### 1-5. codex mcp 설치 및 설정 (교차검증)

```powershell
# Codex CLI 전역 설치 (npm)
npm install -g @openai/codex

# 인증(로그인)
codex login

codex --version    # 설치·버전 확인
```

`.mcp.json`의 `mcpServers`에 `codex` 항목 추가(기존 `ucpm-mcp`와 병기).

```json
{
  "mcpServers": {
    "codex": {
      "command": "codex",
      "args": ["mcp-server"]
    },
    "ucpm-mcp": {
      "command": "ucpm-mcp-server",
      "args": [],
      "env": {
        "UCPM_API_BASE_URL": "http://<서버주소>:<포트>",
        "UCPM_API_KEY": "${UCPM_API_KEY}",
        "UCPM_PROJECT_KEY": "CASESTUDY"
      }
    }
  }
}
```

- 확인: `codex --version` 출력 + `claude` 실행 시 `codex` MCP 도구 인식.

---

## 2. 사용 — 단계별 지시문

각 단계는 필드형 진입 지시문으로 시작한다. `<프로젝트명>`은 실제 대상으로 교체.

### 2-1. 요구사항 분석

```text
[요구분석] <프로젝트명>

- 대상: <시스템 개요 한 줄>
- 배경/필요성: <현황·문제·필요성>
- 진행 방식: `docs/[요구분석]_요구사항분석_가이드.md` 절차에 따라 진행
- 산출물: `docs/[요구분석]_요구사항정의서.md` 양식으로 초안 작성
```

- 회차 진행: AI 초안(v0.1) → 의사결정 요청(D-#) → 사람 결정·추가 지시 → 구체화(v0.2 …) → 사람 최종 승인 시 확정.
- 잔여 미결은 한 번에 몰아 묻지 않고 하나씩 질의응답:

```text
잔여 미결사항에 대한 결정은 하나씩 질의응답 방식으로 진행해줘
```

### 2-2. 설계 — IA

```text
[설계-IA] <프로젝트명>

- 대상: <시스템 개요 한 줄>
- 입력: `docs/[요구분석]_요구사항정의서_<프로젝트명>.md`(승인본)
- 진행 방식: `docs/[설계]_IA_가이드.md` 절차에 따라 진행
- 산출물: `docs/[설계]_IA.md` 양식으로 IA 설계 초안 작성
```

- 요구(FR)에서 데이터·화면·기능 도출 → 추적성(요구↔데이터↔화면↔기능) 매핑 → 사람 최종 승인 시 확정.

### 2-3. 설계 — 화면 프로토타입

```text
[설계-프로토타입] <프로젝트명>

- 대상: <시스템 개요 한 줄>
- 입력: `docs/[요구분석]_요구사항정의서_<프로젝트명>.md`(승인본) · `docs/[설계]_IA.md`(승인본)
- 진행 방식: `docs/[설계]_화면프로토타입_가이드.md` 절차에 따라 진행
- 산출물: Claude Design 프로토타입(`<프로토타입명>_dc_v0.1.md`)
```

- 수정내역 기록 양식:

```text
[수정내역] <프로토타입명>_수정내역_v0.1.md
- 날짜: <YYYY-MM-DD>
- 화면명/ID: <화면명> / <화면 ID>
- 지시내용: <수정 지시>
- 수정내용: <반영 내용>
```

- 반영 위치: 표현·배치·흐름 → 프로토타입 신규 버전 / 구조·데이터·화면 정책 → IA 반영 후 재생성 / 요구 변경·신규 → 요구사항정의서 반영 후 재설계.

### 2-4. 구축 — 초기 구현 계획

```text
[구축-초기계획] <프로젝트명>

- 대상: <시스템 개요 한 줄>
- 입력: `docs/[요구분석]_요구사항정의서_<프로젝트명>.md` · `docs/[설계]_IA_<프로젝트명>.md`(승인본) · 화면 프로토타입(승인본)
- 진행 방식: 입력 기준 단계별 구축 절차·수행 내용 계획 후 절차별 REQ 등록
- 산출물: 구현 절차 + 절차별 REQ
- 원칙: 자료 부족·추론 필요 지점은 임의 확정 없이 권장안·예시로 의사결정 요청

플랜모드로 자체적으로 진행해서 보고해
```

- 승인 후 REQ 등록·스프린트 편성 지시:

```text
승인 REQ 등록 하고
```

```text
Sprint #1, <시작일>부터 <종료일>까지, "<스프린트명>" 스프린트 생성하고 진행중 상태로 변경, 등록된 REQ를 모두 해당 스프린트에 편성해줘
```

### 2-5. 구축 — 실행 (SDD 파이프라인)

REQ를 입력으로 ucpm-agent가 명세화 → 설계 → 구현·검증·커밋 → 교차검증 → 테스트 → 배포를 자율 수행한다. 실행 방식 3종:

| 실행 방식 | 지시문 | Loop 경계 |
|---|---|---|
| 일괄 자동 실행 | `진행중 스프린트 예정 요구사항을 요구사항 등록 순으로 순차적으로 자동으로 실행` | Human out of the loop |
| 단건 자동모드 | `REQ-2026-09-0001 자동 실행` | Human out of the loop |
| 단건 승인모드 | `REQ-2026-09-0001 실행` | Human on the loop |

- **자동모드**: 무인 자율 수행, 사람은 최종 결과물만 검증.
- **승인모드**: 단계 실행 후 승인 대기, 사람이 단계별 결과물 검토·승인/반려.

---

## 3. 참조 문서

가이드·규약의 정본(이 저장소 `docs/` 기준).

| 파일 | 용도 |
|---|---|
| `docs/[GUIDE]_AUTHORING_STYLE.md` | 문서 작성·수정 표준 문체·구조 규칙(모든 가이드의 기준) |
| `docs/UCPM_PIPELINE_GUIDE.md` | R&D 운영 절차 정본(REQ 등록·실행·완료상태 변경, OperationGuide) |
| `docs/[GUIDE]_GIT_BRANCHING.md` | `main`·`dev`·`REQ-####` 브랜치 운영 지침 |
| `docs/[GUIDE]_DEPLOYMENT.md` | dev·prod 2환경 배포 절차·원칙 |
| `docs/[GUIDE]_BACKEND_MIGRATION.md` | Flyway(`V{n}__*.sql`) 스키마 마이그레이션 규약 |
| `docs/[SPEC]_TECH_STACK.md` | 표준 기술 스택·스캐폴딩 반영 범위 |
| `docs/[SPEC]_BACKEND_API.md` | 백엔드 REST API 공통 규격·엔드포인트 명세 |
| `docs/[SPEC]_BACKEND_TABLE.md` | 백엔드 DB 테이블 ERD·컬럼 명세 |
| `docs/[요구분석]_요구사항분석_가이드.md` | 요구사항 분석 절차·역할·작성 규칙 |
| `docs/[요구분석]_요구사항정의서.md` | 요구사항정의서 작성 양식 |
| `docs/[설계]_IA_가이드.md` | IA 설계 절차·역할·작성 방식 |
| `docs/[설계]_IA.md` | IA 설계서 작성 양식 |
| `docs/[설계]_화면프로토타입_가이드.md` | 화면 프로토타입 생성·검토·환류 절차 |
