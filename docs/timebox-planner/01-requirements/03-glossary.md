# 용어 정의서 (Glossary): Timebox Planner

**버전**: v1.0 | **작성일**: 2026-02-28 | **작성자**: PM Agent

---

## 도메인 용어

| 번호 | 용어 (한글) | 영문 | 정의 |
|------|-----------|------|------|
| G-001 | 타임박스 | Timebox | 특정 작업에 할당된 고정된 시간 블록. 시작 시간, 종료 시간, 대상 Task로 구성됨 |
| G-002 | 할 일 | Task | 사용자가 수행해야 할 작업 단위. 제목, 설명, 우선순위, 예상 소요 시간으로 구성됨 |
| G-003 | 집중 세션 | Focus Session | Timebox 타이머를 실행한 집중 시간 단위. 시작/종료 시각, 실제 집중 시간, 상태를 기록함 |
| G-004 | 회고 | Retrospective | 완료된 집중 세션에 대한 만족도 평점과 메모를 남기는 행위 |
| G-005 | 대시보드 | Dashboard | 일별/주별 생산성 통계를 시각화하는 화면 |
| G-006 | 태그 | Tag | 할 일과 타임박스를 분류하기 위한 레이블. 이름과 색상으로 구성됨 |
| G-007 | 완료율 | Completion Rate | 전체 계획된 타임박스 블록 중 완료된 블록의 비율 (%) |
| G-008 | 집중 시간 | Focus Time | 실제 타이머가 실행된 시간의 합계 (일시정지 시간 제외) |
| G-009 | 포모도로 | Pomodoro | 25분 집중 + 5분 휴식 사이클의 시간 관리 기법 (Timebox의 유사 개념) |
| G-010 | 우선순위 | Priority | Task의 중요도 레벨: HIGH(높음) / MEDIUM(중간) / LOW(낮음) |

---

## Task 상태 정의

| 상태 코드 | 상태명 | 설명 |
|---------|-------|------|
| `TODO` | 할 일 | 아직 시작하지 않은 상태 |
| `IN_PROGRESS` | 진행 중 | 타이머가 실행되어 진행 중인 상태 |
| `DONE` | 완료 | 작업이 완료된 상태 |
| `CANCELLED` | 취소 | 사용자가 취소한 상태 |

---

## Timebox 상태 정의

| 상태 코드 | 상태명 | 설명 |
|---------|-------|------|
| `PLANNED` | 계획됨 | 타임박스를 생성했으나 타이머 미시작 |
| `RUNNING` | 실행 중 | 타이머 실행 중 |
| `PAUSED` | 일시정지 | 타이머 일시정지 상태 |
| `COMPLETED` | 완료 | 타이머가 완료된 상태 |
| `CANCELLED` | 취소 | 사용자가 취소한 상태 |

---

## 에러 코드 정의

| 에러 코드 | HTTP 상태 | 설명 |
|---------|---------|------|
| E001 | 409 Conflict | 이미 사용 중인 이메일 |
| E002 | 401 Unauthorized | JWT 토큰 만료 또는 유효하지 않음 |
| E003 | 403 Forbidden | 다른 사용자 리소스 접근 시도 |
| E004 | 409 Conflict | Timebox 시간 중복 |
| E005 | 409 Conflict | 타이머 중복 실행 시도 |
| E006 | 404 Not Found | 리소스를 찾을 수 없음 |
| E007 | 422 Unprocessable Entity | 태그 생성 한도(50개) 초과 |
| E008 | 422 Unprocessable Entity | 회고 작성 기간(24시간) 초과 |
| E009 | 409 Conflict | 실행 중인 타이머가 있는 Timebox 삭제 시도 |
| E010 | 400 Bad Request | 비밀번호 형식 오류 |

---

## 데이터 타입 명세

| 필드명 | 타입 | 형식 | 예시 |
|-------|------|------|-----|
| date | String | YYYY-MM-DD | "2026-02-28" |
| startTime / endTime | String | HH:mm | "09:00", "10:30" |
| createdAt / updatedAt | String | ISO 8601 | "2026-02-28T09:00:00Z" |
| color | String | HEX | "#3B82F6" |
| focusedMinutes | Integer | 분 단위 정수 | 55 |
| completionRate | Double | 소수점 1자리 | 66.7 |
