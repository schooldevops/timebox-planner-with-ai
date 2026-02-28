# 인터페이스 정의서: Timebox Planner

**버전**: v1.0 | **작성일**: 2026-02-28 | **작성자**: Logic Agent (분석 Agent)

---

## 1. API 목록

| API-ID | 엔드포인트 | 메서드 | 설명 | 인증 필요 |
|--------|-----------|--------|------|---------|
| API-001 | /api/v1/auth/register | POST | 회원 가입 | ❌ |
| API-002 | /api/v1/auth/login | POST | 로그인 | ❌ |
| API-003 | /api/v1/auth/logout | POST | 로그아웃 | ✅ |
| API-004 | /api/v1/auth/refresh | POST | 토큰 갱신 | ✅ (Refresh) |
| API-005 | /api/v1/tasks | POST | 할 일 생성 | ✅ |
| API-006 | /api/v1/tasks | GET | 할 일 목록 조회 | ✅ |
| API-007 | /api/v1/tasks/{taskId} | GET | 할 일 단건 조회 | ✅ |
| API-008 | /api/v1/tasks/{taskId} | PUT | 할 일 수정 | ✅ |
| API-009 | /api/v1/tasks/{taskId} | DELETE | 할 일 삭제 | ✅ |
| API-010 | /api/v1/timeboxes | POST | Timebox 블록 생성 | ✅ |
| API-011 | /api/v1/timeboxes | GET | Timebox 목록 조회 (날짜별) | ✅ |
| API-012 | /api/v1/timeboxes/{timeboxId} | GET | Timebox 단건 조회 | ✅ |
| API-013 | /api/v1/timeboxes/{timeboxId} | PATCH | Timebox 시간 변경 | ✅ |
| API-014 | /api/v1/timeboxes/{timeboxId} | DELETE | Timebox 삭제 | ✅ |
| API-015 | /api/v1/timeboxes/{timeboxId}/timer/start | POST | 타이머 시작 | ✅ |
| API-016 | /api/v1/sessions/{sessionId}/pause | POST | 타이머 일시정지 | ✅ |
| API-017 | /api/v1/sessions/{sessionId}/resume | POST | 타이머 재개 | ✅ |
| API-018 | /api/v1/sessions/{sessionId}/complete | POST | 타이머 완료 | ✅ |
| API-019 | /api/v1/sessions/{sessionId}/cancel | POST | 타이머 취소 | ✅ |
| API-020 | /api/v1/sessions/{sessionId}/retrospective | POST | 세션 회고 작성 | ✅ |
| API-021 | /api/v1/tags | POST | 태그 생성 | ✅ |
| API-022 | /api/v1/tags | GET | 태그 목록 조회 | ✅ |
| API-023 | /api/v1/tags/{tagId} | PUT | 태그 수정 | ✅ |
| API-024 | /api/v1/tags/{tagId} | DELETE | 태그 삭제 | ✅ |
| API-025 | /api/v1/dashboard/daily | GET | 일별 대시보드 | ✅ |
| API-026 | /api/v1/dashboard/weekly | GET | 주별 대시보드 | ✅ |

---

## 2. API 상세

### API-001: 회원 가입
- **엔드포인트**: `POST /api/v1/auth/register`
- **인증**: 없음
- **요청**:
  ```json
  {
    "email": "string (이메일 형식, 필수)",
    "password": "string (8자 이상, 영문+숫자+특수문자, 필수)",
    "name": "string (1~50자, 필수)"
  }
  ```
- **응답 201**:
  ```json
  {
    "userId": "long",
    "email": "string",
    "name": "string",
    "createdAt": "string (ISO 8601)"
  }
  ```
- **에러 코드**: E001(409 이메일 중복), E010(400 비밀번호 형식 오류)

---

### API-002: 로그인
- **엔드포인트**: `POST /api/v1/auth/login`
- **인증**: 없음
- **요청**:
  ```json
  {
    "email": "string (필수)",
    "password": "string (필수)"
  }
  ```
- **응답 200**:
  ```json
  {
    "accessToken": "string",
    "refreshToken": "string",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
  ```
- **에러 코드**: E002(401 인증 실패)

---

### API-003: 로그아웃
- **엔드포인트**: `POST /api/v1/auth/logout`
- **인증**: Bearer JWT
- **응답 200**: `{ "message": "로그아웃 완료" }`

---

### API-004: 토큰 갱신
- **엔드포인트**: `POST /api/v1/auth/refresh`
- **인증**: Refresh Token (Request Body)
- **요청**: `{ "refreshToken": "string" }`
- **응답 200**: `{ "accessToken": "string", "expiresIn": 3600 }`
- **에러 코드**: E002(401 토큰 만료)

---

### API-005: 할 일 생성
- **엔드포인트**: `POST /api/v1/tasks`
- **인증**: Bearer JWT
- **요청**:
  ```json
  {
    "title": "string (1~200자, 필수)",
    "description": "string (0~2000자, 선택)",
    "priority": "HIGH | MEDIUM | LOW (기본값: MEDIUM)",
    "estimatedMinutes": "integer (5 단위, 5~480)",
    "tagIds": "array<long> (최대 5개, 선택)"
  }
  ```
- **응답 201**:
  ```json
  {
    "taskId": "long",
    "title": "string",
    "description": "string",
    "priority": "string",
    "estimatedMinutes": "integer",
    "status": "TODO",
    "tags": "array<TagResponse>",
    "createdAt": "string"
  }
  ```

---

### API-006: 할 일 목록 조회
- **엔드포인트**: `GET /api/v1/tasks`
- **인증**: Bearer JWT
- **쿼리 파라미터**:
  - `status`: TODO | IN_PROGRESS | DONE | CANCELLED (선택)
  - `priority`: HIGH | MEDIUM | LOW (선택)
  - `tagId`: long (선택)
  - `page`: integer (기본값: 0)
  - `size`: integer (기본값: 10, 최대: 50)
  - `sort`: createdAt | priority (기본값: createdAt)
- **응답 200**:
  ```json
  {
    "content": "array<TaskResponse>",
    "totalElements": "long",
    "totalPages": "integer",
    "size": "integer",
    "number": "integer"
  }
  ```

---

### API-007: 할 일 단건 조회
- **엔드포인트**: `GET /api/v1/tasks/{taskId}`
- **인증**: Bearer JWT
- **응답 200**: `TaskResponse`
- **에러 코드**: E006(404), E003(403)

---

### API-008: 할 일 수정
- **엔드포인트**: `PUT /api/v1/tasks/{taskId}`
- **인증**: Bearer JWT
- **요청**: CreateTaskRequest 동일 (전체 업데이트)
- **응답 200**: `TaskResponse`
- **에러 코드**: E006(404), E003(403)

---

### API-009: 할 일 삭제
- **엔드포인트**: `DELETE /api/v1/tasks/{taskId}`
- **인증**: Bearer JWT
- **응답 204**: No Content
- **에러 코드**: E006(404), E003(403)

---

### API-010: Timebox 블록 생성
- **엔드포인트**: `POST /api/v1/timeboxes`
- **인증**: Bearer JWT
- **요청**:
  ```json
  {
    "taskId": "long (선택, 미지정 시 빈 블록)",
    "date": "string (YYYY-MM-DD, 필수)",
    "startTime": "string (HH:mm, 30분 단위, 필수)",
    "endTime": "string (HH:mm, 30분 단위, 필수)"
  }
  ```
- **응답 201**:
  ```json
  {
    "timeboxId": "long",
    "taskId": "long | null",
    "taskTitle": "string | null",
    "date": "string",
    "startTime": "string",
    "endTime": "string",
    "status": "PLANNED",
    "createdAt": "string"
  }
  ```
- **에러 코드**: E004(409 시간 중복), E006(404 Task 없음)

---

### API-011: Timebox 목록 조회 (날짜별)
- **엔드포인트**: `GET /api/v1/timeboxes`
- **인증**: Bearer JWT
- **쿼리 파라미터**:
  - `date`: YYYY-MM-DD (필수)
- **응답 200**: `array<TimeboxResponse>`

---

### API-012: Timebox 단건 조회
- **엔드포인트**: `GET /api/v1/timeboxes/{timeboxId}`
- **인증**: Bearer JWT
- **응답 200**: `TimeboxResponse`
- **에러 코드**: E006(404), E003(403)

---

### API-013: Timebox 시간 변경
- **엔드포인트**: `PATCH /api/v1/timeboxes/{timeboxId}`
- **인증**: Bearer JWT
- **요청**:
  ```json
  {
    "startTime": "string (HH:mm, 선택)",
    "endTime": "string (HH:mm, 선택)",
    "taskId": "long | null (선택)"
  }
  ```
- **응답 200**: `TimeboxResponse`
- **에러 코드**: E004(409 중복), E009(409 타이머 실행 중)

---

### API-014: Timebox 삭제
- **엔드포인트**: `DELETE /api/v1/timeboxes/{timeboxId}`
- **인증**: Bearer JWT
- **응답 204**: No Content
- **에러 코드**: E009(409 타이머 실행 중 삭제 불가), E003(403)

---

### API-015: 타이머 시작
- **엔드포인트**: `POST /api/v1/timeboxes/{timeboxId}/timer/start`
- **인증**: Bearer JWT
- **응답 200**:
  ```json
  {
    "sessionId": "long",
    "timeboxId": "long",
    "status": "RUNNING",
    "startedAt": "string (ISO 8601)",
    "plannedMinutes": "integer"
  }
  ```
- **에러 코드**: E005(409 타이머 중복), E006(404)

---

### API-016 ~ API-019: 타이머 상태 변경
- **pause**: `POST /api/v1/sessions/{sessionId}/pause` → `{ "status": "PAUSED", "pausedAt": "string" }`
- **resume**: `POST /api/v1/sessions/{sessionId}/resume` → `{ "status": "RUNNING" }`
- **complete**: `POST /api/v1/sessions/{sessionId}/complete`
  - 요청: `{ "confirmDone": boolean }`
  - 응답: `{ "status": "COMPLETED", "focusedMinutes": "integer", "completedAt": "string" }`
- **cancel**: `POST /api/v1/sessions/{sessionId}/cancel` → `{ "status": "CANCELLED" }`

---

### API-020: 세션 회고 작성
- **엔드포인트**: `POST /api/v1/sessions/{sessionId}/retrospective`
- **인증**: Bearer JWT
- **요청**:
  ```json
  {
    "rating": "integer (1~5, 필수)",
    "memo": "string (0~500자, 선택)"
  }
  ```
- **응답 201**: `{ "retrospectiveId": "long", "rating": "integer", "memo": "string", "createdAt": "string" }`
- **에러 코드**: E008(422 24시간 초과)

---

### API-021: 태그 생성
- **엔드포인트**: `POST /api/v1/tags`
- **인증**: Bearer JWT
- **요청**: `{ "name": "string (1~30자)", "color": "string (HEX 6자리, 선택)" }`
- **응답 201**: `{ "tagId": "long", "name": "string", "color": "string" }`
- **에러 코드**: E007(422 50개 초과)

---

### API-022 ~ API-024: 태그 CRUD
- **목록 조회** `GET /api/v1/tags` → `array<TagResponse>`
- **수정** `PUT /api/v1/tags/{tagId}` → `TagResponse`
- **삭제** `DELETE /api/v1/tags/{tagId}` → 204

---

### API-025: 일별 대시보드
- **엔드포인트**: `GET /api/v1/dashboard/daily`
- **쿼리 파라미터**: `date: YYYY-MM-DD`
- **응답 200**:
  ```json
  {
    "date": "string",
    "totalBlocks": "integer",
    "completedBlocks": "integer",
    "completionRate": "double",
    "plannedMinutes": "integer",
    "focusedMinutes": "integer",
    "timeboxes": "array<TimeboxSummary>"
  }
  ```

---

### API-026: 주별 대시보드
- **엔드포인트**: `GET /api/v1/dashboard/weekly`
- **쿼리 파라미터**: `startDate: YYYY-MM-DD`
- **응답 200**:
  ```json
  {
    "startDate": "string",
    "endDate": "string",
    "totalFocusedMinutes": "integer",
    "dailyStats": "array<DailyStat>",
    "topTags": "array<TagStat>"
  }
  ```

---

## 3. 공통 에러 응답 형식

```json
{
  "error": "string (에러 코드, 예: E001)",
  "message": "string (사용자 친화적 메시지)",
  "timestamp": "string (ISO 8601)",
  "path": "string (요청 경로)"
}
```

---

## 4. 페이징, 정렬, 필터링 규칙

- 페이징: `page` (0-based), `size` (기본 10, 최대 50)
- 정렬: `sort=field,direction` (예: `sort=createdAt,desc`)
- 날짜 필터: `startDate` ~ `endDate` (YYYY-MM-DD)
- 응답은 Spring Page 표준 형태 준수
