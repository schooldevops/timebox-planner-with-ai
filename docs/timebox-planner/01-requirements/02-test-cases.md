# 테스트 케이스: Timebox Planner

**버전**: v1.0 | **작성일**: 2026-02-28 | **작성자**: PM Agent

---

## TC 목록

| TC-ID | 시나리오 | 관련 FR | 우선순위 |
|-------|---------|--------|--------|
| TC-001 | 정상적인 회원 가입 | FR-001 | Critical |
| TC-002 | 중복 이메일 회원 가입 시도 | FR-001 | Critical |
| TC-003 | 이메일/비밀번호로 로그인 성공 | FR-001 | Critical |
| TC-004 | 할 일 생성 (정상) | FR-002 | Critical |
| TC-005 | 할 일 조회 (목록) | FR-002 | Critical |
| TC-006 | 할 일 수정 | FR-002 | High |
| TC-007 | 할 일 삭제 | FR-002 | High |
| TC-008 | Timebox 블록 생성 (정상) | FR-003 | Critical |
| TC-009 | 동일 시간대 중복 블록 생성 시도 | FR-003 | Critical |
| TC-010 | Timebox 블록 이동 (시간 변경) | FR-003 | High |
| TC-011 | 타이머 시작 | FR-004 | Critical |
| TC-012 | 타이머 일시정지 및 재개 | FR-004 | High |
| TC-013 | 타이머 완료 시 Task 상태 변경 | FR-004 | Critical |
| TC-014 | 일별 대시보드 완료율 조회 | FR-005 | High |
| TC-015 | 주별 집중 시간 통계 조회 | FR-005 | Medium |
| TC-016 | 태그 생성 및 조회 | FR-006 | High |
| TC-017 | 태그 50개 초과 생성 시도 | FR-006 | High |
| TC-018 | 집중 세션 회고 작성 (정상) | FR-007 | Medium |
| TC-019 | 24시간 경과 후 회고 작성 시도 | FR-007 | Medium |
| TC-020 | 만료된 JWT로 API 호출 | FR-001 | Critical |

---

## 상세 테스트 케이스

### TC-001: 정상적인 회원 가입
- **관련 요구사항**: FR-001
- **우선순위**: Critical
- **전제조건(Given)**: 시스템에 등록되지 않은 이메일
- **테스트 단계(When)**:
  1. `POST /api/v1/auth/register` 호출
  2. Body: `{ "email": "test@example.com", "password": "Test1234!", "name": "테스터" }`
- **예상 결과(Then)**:
  - HTTP 201 Created 반환
  - 응답에 `userId`, `email`, `name` 포함
  - 비밀번호는 응답에 미포함

### TC-002: 중복 이메일 회원 가입 시도
- **관련 요구사항**: FR-001, EC-001
- **우선순위**: Critical
- **전제조건(Given)**: `test@example.com` 이미 등록된 상태
- **테스트 단계(When)**:
  1. `POST /api/v1/auth/register` 동일 이메일로 호출
- **예상 결과(Then)**:
  - HTTP 409 Conflict 반환
  - `{ "error": "E001", "message": "이미 사용 중인 이메일입니다" }`

### TC-003: 이메일/비밀번호로 로그인 성공
- **관련 요구사항**: FR-001
- **우선순위**: Critical
- **전제조건(Given)**: 등록된 사용자 계정
- **테스트 단계(When)**:
  1. `POST /api/v1/auth/login` 호출
  2. Body: `{ "email": "test@example.com", "password": "Test1234!" }`
- **예상 결과(Then)**:
  - HTTP 200 OK 반환
  - `{ "accessToken": "...", "refreshToken": "...", "tokenType": "Bearer" }`

### TC-004: 할 일 생성 (정상)
- **관련 요구사항**: FR-002
- **우선순위**: Critical
- **전제조건(Given)**: 로그인된 사용자 (Bearer 토큰 보유)
- **테스트 단계(When)**:
  1. `POST /api/v1/tasks` 호출
  2. Body: `{ "title": "API 개발", "priority": "HIGH", "estimatedMinutes": 60 }`
- **예상 결과(Then)**:
  - HTTP 201 Created 반환
  - `taskId`, `title`, `status: "TODO"`, `createdAt` 포함

### TC-005: 할 일 조회 (목록)
- **관련 요구사항**: FR-002
- **우선순위**: Critical
- **전제조건(Given)**: 로그인된 사용자, 할 일 3개 등록된 상태
- **테스트 단계(When)**:
  1. `GET /api/v1/tasks?status=TODO&page=0&size=10` 호출
- **예상 결과(Then)**:
  - HTTP 200 OK 반환
  - `{ "content": [...], "totalElements": 3, "totalPages": 1 }`

### TC-006: 할 일 수정
- **관련 요구사항**: FR-002
- **우선순위**: High
- **전제조건(Given)**: 로그인된 사용자, 본인 소유 Task 존재
- **테스트 단계(When)**:
  1. `PUT /api/v1/tasks/{taskId}` 호출
  2. Body: `{ "title": "API 개발 (수정)", "priority": "MEDIUM" }`
- **예상 결과(Then)**:
  - HTTP 200 OK 반환
  - 수정된 Task 정보 반환

### TC-007: 할 일 삭제
- **관련 요구사항**: FR-002
- **우선순위**: High
- **전제조건(Given)**: 로그인된 사용자, 본인 소유 Task 존재 (상태: TODO)
- **테스트 단계(When)**:
  1. `DELETE /api/v1/tasks/{taskId}` 호출
- **예상 결과(Then)**:
  - HTTP 204 No Content 반환

### TC-008: Timebox 블록 생성 (정상)
- **관련 요구사항**: FR-003
- **우선순위**: Critical
- **전제조건(Given)**: 로그인된 사용자, Task 존재, 해당 날짜/시간 비어있음
- **테스트 단계(When)**:
  1. `POST /api/v1/timeboxes` 호출
  2. Body: `{ "taskId": 1, "date": "2026-02-28", "startTime": "09:00", "endTime": "10:00" }`
- **예상 결과(Then)**:
  - HTTP 201 Created
  - `timeboxId`, `taskId`, `date`, `startTime`, `endTime`, `status: "PLANNED"` 반환

### TC-009: 동일 시간대 중복 블록 생성 시도
- **관련 요구사항**: FR-003, EC-004
- **우선순위**: Critical
- **전제조건(Given)**: 09:00~10:00 블록이 이미 존재
- **테스트 단계(When)**:
  1. 동일 날짜 09:30~10:30 블록 생성 시도
- **예상 결과(Then)**:
  - HTTP 409 Conflict
  - `{ "error": "E004", "message": "해당 시간대에 이미 등록된 Timebox가 있습니다" }`

### TC-010: Timebox 블록 이동 (시간 변경)
- **관련 요구사항**: FR-003
- **우선순위**: High
- **전제조건(Given)**: 기존 블록 존재, 이동할 시간대 비어있음
- **테스트 단계(When)**:
  1. `PATCH /api/v1/timeboxes/{timeboxId}` 호출
  2. Body: `{ "startTime": "14:00", "endTime": "15:00" }`
- **예상 결과(Then)**:
  - HTTP 200 OK, 변경된 시간 정보 반환

### TC-011: 타이머 시작
- **관련 요구사항**: FR-004
- **우선순위**: Critical
- **전제조건(Given)**: PLANNED 상태의 Timebox 블록, 실행 중인 타이머 없음
- **테스트 단계(When)**:
  1. `POST /api/v1/timeboxes/{timeboxId}/timer/start` 호출
- **예상 결과(Then)**:
  - HTTP 200 OK
  - `{ "sessionId": "...", "status": "RUNNING", "startedAt": "..." }`

### TC-012: 타이머 일시정지 및 재개
- **관련 요구사항**: FR-004
- **우선순위**: High
- **전제조건(Given)**: RUNNING 상태의 타이머 세션
- **테스트 단계(When)**:
  1. `POST /api/v1/sessions/{sessionId}/pause` 호출 → 상태 PAUSED 확인
  2. `POST /api/v1/sessions/{sessionId}/resume` 호출 → 상태 RUNNING 확인
- **예상 결과(Then)**:
  - 각각 HTTP 200 OK, 상태 변경 확인

### TC-013: 타이머 완료 시 Task 상태 변경
- **관련 요구사항**: FR-004
- **우선순위**: Critical
- **전제조건(Given)**: RUNNING 상태의 세션
- **테스트 단계(When)**:
  1. `POST /api/v1/sessions/{sessionId}/complete` 호출
  2. Body: `{ "confirmDone": true }`
- **예상 결과(Then)**:
  - HTTP 200 OK
  - Task status: DONE, Session status: COMPLETED
  - `focusedMinutes` (실제 집중 시간) 기록

### TC-014: 일별 대시보드 완료율 조회
- **관련 요구사항**: FR-005
- **우선순위**: High
- **전제조건(Given)**: 2026-02-28에 5개 블록 중 3개 완료된 상태
- **테스트 단계(When)**:
  1. `GET /api/v1/dashboard/daily?date=2026-02-28` 호출
- **예상 결과(Then)**:
  - HTTP 200 OK
  - `{ "completionRate": 60.0, "totalBlocks": 5, "completedBlocks": 3, "focusedMinutes": 180 }`

### TC-015: 주별 집중 시간 통계 조회
- **관련 요구사항**: FR-005
- **우선순위**: Medium
- **전제조건(Given)**: 최근 7일 세션 데이터 존재
- **테스트 단계(When)**:
  1. `GET /api/v1/dashboard/weekly?startDate=2026-02-22` 호출
- **예상 결과(Then)**:
  - HTTP 200 OK
  - 일별 집중 시간 배열, 총 집중 시간, Top 5 태그 반환

### TC-016: 태그 생성 및 조회
- **관련 요구사항**: FR-006
- **우선순위**: High
- **전제조건(Given)**: 로그인된 사용자
- **테스트 단계(When)**:
  1. `POST /api/v1/tags` Body: `{ "name": "개발", "color": "#3B82F6" }`
  2. `GET /api/v1/tags` 목록 조회
- **예상 결과(Then)**:
  - 생성: HTTP 201, 조회: HTTP 200

### TC-017: 태그 50개 초과 생성 시도
- **관련 요구사항**: FR-006, EC-007
- **우선순위**: High
- **전제조건(Given)**: 50개 태그 이미 존재
- **테스트 단계(When)**:
  1. 51번째 태그 생성 시도
- **예상 결과(Then)**:
  - HTTP 422, `{ "error": "E007", "message": "태그는 최대 50개까지 생성 가능합니다" }`

### TC-018: 집중 세션 회고 작성 (정상)
- **관련 요구사항**: FR-007
- **우선순위**: Medium
- **전제조건(Given)**: 방금 완료된 세션 (24시간 이내)
- **테스트 단계(When)**:
  1. `POST /api/v1/sessions/{sessionId}/retrospective`
  2. Body: `{ "rating": 4, "memo": "집중 잘 됐음" }`
- **예상 결과(Then)**:
  - HTTP 201, 회고 데이터 반환

### TC-019: 24시간 경과 후 회고 작성 시도
- **관련 요구사항**: FR-007, EC-008
- **우선순위**: Medium
- **전제조건(Given)**: 25시간 전에 완료된 세션
- **테스트 단계(When)**:
  1. 해당 세션에 회고 작성 시도
- **예상 결과(Then)**:
  - HTTP 422, `{ "error": "E008", "message": "회고는 세션 완료 후 24시간 이내에만 작성 가능합니다" }`

### TC-020: 만료된 JWT로 API 호출
- **관련 요구사항**: FR-001, EC-002
- **우선순위**: Critical
- **전제조건(Given)**: 유효 기간이 지난 Access Token
- **테스트 단계(When)**:
  1. 만료된 토큰으로 `GET /api/v1/tasks` 호출
- **예상 결과(Then)**:
  - HTTP 401 Unauthorized
  - `{ "error": "E002", "message": "인증 토큰이 만료되었습니다. 다시 로그인해주세요." }`
