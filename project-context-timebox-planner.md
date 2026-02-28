# Project Context: Timebox Planner

## 프로젝트 정보
- **프로젝트명**: timebox-planner
- **요청 부서**: 자기계발/생산성팀
- **시작일**: 2026-02-28
- **현재 단계**: Step 2 - 분석 완료, Step 3 - 설계 대기 중
- **Git Repository**: git@github.com:schooldevops/timebox-planner-with-ai.git

## 완료된 산출물

- [x] 인터페이스 정의서 (v1.0) - `docs/timebox-planner/02-analysis/01-interface-spec.md`
  - 26개 API 엔드포인트 정의
  - 요청/응답 스키마 상세, 에러 코드
- [x] 비즈니스 로직 상세 (v1.0) - `docs/timebox-planner/02-analysis/02-business-logic-detail.md`
  - 8개 핵심 BL (회원가입, 로그인, Task, Timebox, 타이머, 대시보드, 회고)
  - 트랜잭션 경계 정의
- [x] 데이터 모델 (v1.0) - `docs/timebox-planner/02-analysis/03-data-model.md`
  - 8개 엔티티 (users, tags, tasks, task_tags, timeboxes, focus_sessions, retrospectives, refresh_tokens)
  - Mermaid ERD, 인덱스 전략
- [x] 시퀀스 다이어그램 (v1.0) - `docs/timebox-planner/02-analysis/04-sequence-diagrams.md`
  - 7개 주요 플로우 (회원가입, 로그인, Timebox 생성, 타이머 시작→완료, 대시보드, 토큰 갱신, 회고)

## Step 1 완료 산출물

- [x] 요건정의서 (v1.0) - `docs/timebox-planner/01-requirements/01-requirements-spec.md`
  - 7개 기능 요구사항 (FR-001 ~ FR-007)
  - 5개 비기능 요구사항 (NFR-001 ~ NFR-005)
  - 10개 핵심 예외 케이스 (EC-001 ~ EC-010)
- [x] 테스트 케이스 (v1.0) - `docs/timebox-planner/01-requirements/02-test-cases.md`
  - 20개 TC (TC-001 ~ TC-020)
  - Given-When-Then 상세 시나리오
- [x] 용어 정의서 (v1.0) - `docs/timebox-planner/01-requirements/03-glossary.md`
  - 10개 도메인 용어
  - Task/Timebox 상태 정의
  - 에러 코드 10개
  - 데이터 타입 명세

## 주요 요구사항 요약

### 기능 요구사항
- FR-001: 회원 가입 및 로그인 (JWT 인증) - **P0**
- FR-002: 할 일(Task) CRUD - **P0**
- FR-003: Timebox(시간 블록) 계획 - **P0**
- FR-004: 집중 타이머 (타이머 시작/정지/완료) - **P0**
- FR-005: 일별/주별 대시보드 - **P1**
- FR-006: 태그 관리 - **P1**
- FR-007: 집중 세션 회고 - **P2**

### 비기능 요구사항
- 응답 시간: 95% 요청 200ms 이내
- 보안: ISMS-P 준수, JWT, BCrypt, XSS/SQL Injection 방지
- 가용성: 99.5% 이상

## 기술 스택 (예정)
- Backend: Kotlin + Spring Boot 3.x + jOOQ + PostgreSQL
- Frontend: Next.js 14+ + Tailwind CSS v4 + shadcn/ui + TanStack Query

## 산출물 디렉토리 구조
```
docs/timebox-planner/
├── 01-requirements/         ✅ 완료
│   ├── 01-requirements-spec.md
│   ├── 02-test-cases.md
│   └── 03-glossary.md
├── 02-analysis/             ✅ 완료
├── 03-design/               ⏳ 대기 (Step 3)
├── 04-dev/                  ⏳ 대기
├── 05-qa/                   ⏳ 대기
└── 06-artifacts/            ⏳ 대기
```
