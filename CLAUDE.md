# CLAUDE.md

## 프로젝트 개요

> **UCPM 연계 웹 개발 표준 템플릿**으로 신규 프로젝트 생성 시 스캐폴딩 및 UCPM 기반 AI 개발 파이프라인 적용 설정을 빠르게 수행하기 위한 프로젝트 표준 템플릿임

- 초기 프로젝트 구성을 위한 절차 Bootstrap 가이드
- 웹 개발을 위한 Frontend, Backend 스케폴링 
- UCPM 개발 파이프라인 연계를 위한 지침

## 참조 문서 인덱스

규약·절차의 정본은 이 템플릿 내부 문서다. 아래는 현재 존재하는 문서 기준이다.

| 주제 | 정본 경로 |
|---|---|
| 요구사항 분석 가이드 | `docs/[요구분석]_요구사항분석_가이드.md` |
| 요구사항 정의서 | `docs/[요구분석]_요구사항정의서.md` |
| 화면 프로토타입 가이드 | `docs/[설계]_화면프로토타입_가이드.md` |
| IA(정보구조) 가이드 | `docs/[설계]_IA_가이드.md` |
| IA(정보구조) | `docs/[설계]_IA.md` |
| 백엔드 빌드·실행·구조·엔드포인트 | `backend/README.md` |
| 백엔드 API 명세(요청·응답·에러) | `docs/[SPEC]_BACKEND_API.md` |
| 백엔드 테이블 명세(ERD·컬럼) | `docs/[SPEC]_BACKEND_TABLE.md` |
| 프런트엔드 실행·UI 공통요소·구조 | `frontend/README.md` |
| 기술 스택 | `docs/[SPEC]_TECH_STACK.md` |
| UCPM-PIPELINE R&D 운영 절차 | `docs/UCPM_PIPELINE_GUIDE.md` |
| Git 브랜치·병합·커밋·푸시 | `docs/[GUIDE]_GIT_BRANCHING.md` |
| 배포 절차 | `docs/[GUIDE]_DEPLOYMENT.md` |
| DB 스키마 마이그레이션(Flyway) | `docs/[GUIDE]_BACKEND_MIGRATION.md` |
| 가이드 작성 방식(문체·구조) | `docs/[GUIDE]_AUTHORING_STYLE.md` |

## Claude Design 연계 (프로토타입)

`mcp__claude-design__*` 도구로 프로토타입에 접근할 때 아래 `project_id`(키)를 사용한다.
파일 조회·읽기는 `list_files` / `read_file` 에 이 키를 넘긴다.

| 프로젝트 | project_id (MCP 키) | URL |
|---|---|---|
| <프로젝트명> | `<project_id>` | <프로토타입 URL> |

**프로토타입 파일** (project_id `<project_id>` 기준)

| 파일명 | 용도 |
|---|---|
| `<파일명>` | <용도 설명> |

## OperationGuide
docs/UCPM_PIPELINE_GUIDE.md
