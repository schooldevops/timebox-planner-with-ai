# 데이터 모델: Timebox Planner

**버전**: v1.0 | **작성일**: 2026-02-28 | **작성자**: Logic Agent

---

## 1. 엔티티 목록

| 엔티티 | 테이블명 | 설명 |
|-------|--------|------|
| User | users | 서비스 사용자 |
| Tag | tags | 사용자 정의 태그 |
| Task | tasks | 할 일 항목 |
| TaskTag | task_tags | Task-Tag 연관 (N:M) |
| Timebox | timeboxes | 시간 블록 |
| FocusSession | focus_sessions | 집중 타이머 세션 |
| Retrospective | retrospectives | 세션 회고 |
| RefreshToken | refresh_tokens | JWT Refresh Token |

---

## 2. ERD

```mermaid
erDiagram
    users {
        bigint id PK "사용자 ID"
        varchar email UK "로그인 이메일"
        varchar password "BCrypt 암호화된 비밀번호"
        varchar name "사용자 이름"
        timestamp created_at "가입 일시"
        timestamp updated_at "최종 수정 일시"
    }

    tags {
        bigint id PK "태그 ID"
        bigint user_id FK "소유자"
        varchar name "태그명"
        varchar color "HEX 색상코드"
        timestamp created_at "생성 일시"
        timestamp updated_at "수정 일시"
    }

    tasks {
        bigint id PK "할 일 ID"
        bigint user_id FK "소유자"
        varchar title "제목"
        text description "설명"
        varchar priority "우선순위 HIGH,MEDIUM,LOW"
        integer estimated_minutes "예상 소요 분"
        varchar status "상태 TODO,IN_PROGRESS,DONE,CANCELLED"
        timestamp created_at "생성 일시"
        timestamp updated_at "수정 일시"
    }

    task_tags {
        bigint task_id FK "할 일 참조"
        bigint tag_id FK "태그 참조"
    }

    timeboxes {
        bigint id PK "타임박스 ID"
        bigint user_id FK "소유자"
        bigint task_id FK "연결된 할 일"
        date date "날짜"
        varchar start_time "시작 시간 HH:mm"
        varchar end_time "종료 시간 HH:mm"
        varchar status "상태 PLANNED,RUNNING,PAUSED,COMPLETED,CANCELLED"
        timestamp created_at "생성 일시"
        timestamp updated_at "수정 일시"
    }

    focus_sessions {
        bigint id PK "세션 ID"
        bigint user_id FK "소유자"
        bigint timebox_id FK "타임박스 참조"
        varchar status "상태 RUNNING,PAUSED,COMPLETED,CANCELLED"
        integer planned_minutes "계획 분"
        integer focused_minutes "실제 집중 분"
        integer total_paused_minutes "일시정지 누적 분"
        timestamp started_at "시작 일시"
        timestamp paused_at "일시정지 일시"
        timestamp completed_at "완료 일시"
        timestamp created_at "생성 일시"
        timestamp updated_at "수정 일시"
    }

    retrospectives {
        bigint id PK "회고 ID"
        bigint session_id FK "세션 참조"
        smallint rating "만족도 1~5점"
        text memo "회고 메모"
        timestamp created_at "생성 일시"
        timestamp updated_at "수정 일시"
    }

    refresh_tokens {
        bigint id PK "토큰 ID"
        bigint user_id FK "소유자"
        text token UK "Refresh Token 값"
        timestamp expires_at "만료 일시"
        timestamp created_at "생성 일시"
    }

    users ||--o{ tags : "소유"
    users ||--o{ tasks : "소유"
    users ||--o{ timeboxes : "소유"
    users ||--o{ focus_sessions : "소유"
    users ||--o{ refresh_tokens : "보유"
    tasks ||--o{ task_tags : "포함"
    tags ||--o{ task_tags : "포함"
    tasks ||--o{ timeboxes : "연결"
    timeboxes ||--o{ focus_sessions : "실행"
    focus_sessions ||--o| retrospectives : "회고"
```

---

## 3. 엔티티 상세 정의

### 3.1 users 테이블

| 컬럼 | 타입 | 제약조건 | 설명 |
|-----|------|---------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 사용자 ID |
| email | VARCHAR(255) | NOT NULL, UNIQUE | 로그인 이메일 (소문자 정규화) |
| password | VARCHAR(255) | NOT NULL | BCrypt 해시 비밀번호 |
| name | VARCHAR(100) | NOT NULL | 사용자 이름 |
| created_at | TIMESTAMP | NOT NULL, DEFAULT now() | 가입 일시 |
| updated_at | TIMESTAMP | NOT NULL | 최종 수정 일시 |

### 3.2 tasks 테이블

| 컬럼 | 타입 | 제약조건 | 설명 |
|-----|------|---------|------|
| id | BIGINT | PK, AUTO_INCREMENT | Task ID |
| user_id | BIGINT | NOT NULL, FK → users.id | 소유자 |
| title | VARCHAR(200) | NOT NULL | 제목 |
| description | TEXT | NULL | 설명 |
| priority | VARCHAR(20) | NOT NULL, DEFAULT 'MEDIUM' | HIGH/MEDIUM/LOW |
| estimated_minutes | INTEGER | NULL, CHECK (% 5 = 0) | 예상 소요 시간 |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'TODO' | 상태 |
| created_at | TIMESTAMP | NOT NULL, DEFAULT now() | |
| updated_at | TIMESTAMP | NOT NULL | |

### 3.3 timeboxes 테이블

| 컬럼 | 타입 | 제약조건 | 설명 |
|-----|------|---------|------|
| id | BIGINT | PK, AUTO_INCREMENT | Timebox ID |
| user_id | BIGINT | NOT NULL, FK → users.id | 소유자 |
| task_id | BIGINT | NULL, FK → tasks.id | 연결 Task |
| date | DATE | NOT NULL | 날짜 |
| start_time | TIME | NOT NULL | 시작 시간 (30분 단위) |
| end_time | TIME | NOT NULL | 종료 시간 (30분 단위) |
| status | VARCHAR(20) | NOT NULL, DEFAULT 'PLANNED' | 상태 |
| created_at | TIMESTAMP | NOT NULL | |
| updated_at | TIMESTAMP | NOT NULL | |

**제약조건**: `UNIQUE(user_id, date, start_time)` (중복 방지)

### 3.4 focus_sessions 테이블

| 컬럼 | 타입 | 제약조건 | 설명 |
|-----|------|---------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 세션 ID |
| user_id | BIGINT | NOT NULL, FK | 소유자 |
| timebox_id | BIGINT | NOT NULL, FK → timeboxes.id | 연결 Timebox |
| status | VARCHAR(20) | NOT NULL | 세션 상태 |
| planned_minutes | INTEGER | NOT NULL | 계획 분 |
| focused_minutes | INTEGER | NULL | 실제 집중 분 (완료 시 계산) |
| total_paused_minutes | INTEGER | NOT NULL, DEFAULT 0 | 일시정지 누적 시간 |
| started_at | TIMESTAMP | NOT NULL | 시작 일시 |
| paused_at | TIMESTAMP | NULL | 일시정지 시작 일시 |
| completed_at | TIMESTAMP | NULL | 완료 일시 |

---

## 4. 인덱스 전략

| 테이블 | 인덱스 | 컬럼 | 목적 |
|-------|-------|------|------|
| tasks | idx_tasks_user_status | (user_id, status) | 사용자별 상태 필터링 |
| tasks | idx_tasks_user_priority | (user_id, priority) | 우선순위 정렬 |
| timeboxes | idx_timeboxes_user_date | (user_id, date) | 날짜별 조회 |
| timeboxes | idx_timeboxes_overlap_check | (user_id, date, start_time, end_time) | 중복 방지 |
| focus_sessions | idx_sessions_user_status | (user_id, status) | 실행 중 세션 조회 |
| focus_sessions | idx_sessions_timebox | (timebox_id) | Timebox별 세션 조회 |
| tags | idx_tags_user_id | (user_id) | 사용자 태그 전체 조회 |
| refresh_tokens | idx_refresh_tokens_user | (user_id) | 사용자 토큰 조회 |
