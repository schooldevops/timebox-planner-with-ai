# 시퀀스 다이어그램: Timebox Planner

**버전**: v1.0 | **작성일**: 2026-02-28 | **작성자**: Logic Agent

---

## SD-001: 회원 가입 흐름

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend (Next.js)
    participant BE as Backend (Spring Boot)
    participant DB as PostgreSQL

    User->>FE: 이메일, 비밀번호, 이름 입력
    FE->>BE: POST /api/v1/auth/register
    BE->>BE: 입력값 검증 (이메일 형식, 비밀번호 형식)
    BE->>DB: SELECT 이메일 중복 확인
    alt 이메일 중복
        DB-->>BE: 중복 결과
        BE-->>FE: 409 Conflict (E001)
        FE-->>User: "이미 사용 중인 이메일입니다" 표시
    else 신규
        DB-->>BE: 없음
        BE->>BE: BCrypt 비밀번호 암호화
        BE->>DB: INSERT INTO users
        DB-->>BE: 저장 완료
        BE-->>FE: 201 Created (UserResponse)
        FE-->>User: 회원가입 성공, 로그인 페이지 이동
    end
```

---

## SD-002: 로그인 및 JWT 발급

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend
    participant BE as Backend
    participant DB as PostgreSQL

    User->>FE: 이메일/비밀번호 입력
    FE->>BE: POST /api/v1/auth/login
    BE->>DB: SELECT * FROM users WHERE email = ?
    alt 사용자 없음 또는 비밀번호 불일치
        BE-->>FE: 401 Unauthorized (E002)
        FE-->>User: "이메일 또는 비밀번호가 올바르지 않습니다"
    else 인증 성공
        BE->>BE: Access Token 생성 (1h)
        BE->>BE: Refresh Token 생성 (7d)
        BE->>DB: INSERT INTO refresh_tokens
        BE-->>FE: 200 OK (accessToken, refreshToken)
        FE->>FE: 토큰 저장 (메모리/쿠키)
        FE-->>User: 대시보드로 이동
    end
```

---

## SD-003: Timebox 블록 생성

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend
    participant BE as Backend
    participant DB as PostgreSQL

    User->>FE: 날짜, 시간, Task 선택
    FE->>BE: POST /api/v1/timeboxes (JWT)
    BE->>BE: JWT 검증
    BE->>BE: 입력값 검증 (30분 단위, startTime < endTime)
    BE->>DB: SELECT 시간 중복 조회 (user_id, date, time range)
    alt 시간 중복 존재
        DB-->>BE: 중복 결과
        BE-->>FE: 409 Conflict (E004)
        FE-->>User: "해당 시간에 이미 블록이 있습니다"
    else 중복 없음
        BE->>DB: 하루 블록 수 확인 (≤ 16개)
        alt 블록 16개 초과
            BE-->>FE: 422 Unprocessable Entity
            FE-->>User: "하루 최대 16개 블록까지 등록 가능합니다"
        else
            BE->>DB: INSERT INTO timeboxes
            DB-->>BE: 저장 완료
            BE-->>FE: 201 Created (TimeboxResponse)
            FE-->>User: 타임라인에 블록 표시
        end
    end
```

---

## SD-004: 집중 타이머 시작 → 완료

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend (Timer)
    participant BE as Backend
    participant DB as PostgreSQL

    User->>FE: 타이머 시작 버튼 클릭
    FE->>BE: POST /api/v1/timeboxes/{id}/timer/start (JWT)
    BE->>DB: 실행 중 세션 조회 (user_id, status=RUNNING)
    alt 이미 실행 중인 타이머
        BE-->>FE: 409 Conflict (E005)
        FE-->>User: "이미 실행 중인 타이머가 있습니다"
    else 없음
        BE->>DB: INSERT INTO focus_sessions (RUNNING)
        BE->>DB: UPDATE timeboxes SET status=RUNNING
        BE->>DB: UPDATE tasks SET status=IN_PROGRESS
        BE-->>FE: 200 OK (sessionId, plannedMinutes)
        FE->>FE: 카운트다운 타이머 시작 (JavaScript)
        FE-->>User: 타이머 화면 표시
    end

    Note over FE: 타이머 카운트다운 중 (클라이언트)

    alt 사용자가 일시정지
        User->>FE: 일시정지 버튼 클릭
        FE->>BE: POST /api/v1/sessions/{id}/pause
        BE->>DB: UPDATE focus_sessions SET status=PAUSED, paused_at=now()
        BE-->>FE: 200 OK (PAUSED)
        FE->>FE: 타이머 정지
    end

    alt 타이머 완료 (시간 경과 or 완료 버튼)
        FE->>BE: POST /api/v1/sessions/{id}/complete (confirmDone: true)
        BE->>BE: focusedMinutes 계산
        BE->>DB: UPDATE focus_sessions SET status=COMPLETED, focused_minutes=?
        BE->>DB: UPDATE timeboxes SET status=COMPLETED
        BE->>DB: UPDATE tasks SET status=DONE (confirmDone=true)
        BE-->>FE: 200 OK (focusedMinutes)
        FE-->>User: 완료! 회고 작성 프롬프트 표시
    end
```

---

## SD-005: 일별 대시보드 조회

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend
    participant BE as Backend
    participant DB as PostgreSQL

    User->>FE: 날짜 선택
    FE->>BE: GET /api/v1/dashboard/daily?date=YYYY-MM-DD (JWT)
    BE->>DB: SELECT timeboxes WHERE user_id=? AND date=?
    BE->>DB: SELECT focus_sessions JOIN timeboxes WHERE date=?
    DB-->>BE: timeboxes 목록, sessions 목록
    BE->>BE: 집계 계산
    Note right of BE: completionRate = COMPLETED/전체 * 100
    Note right of BE: focusedMinutes = Σ focused_minutes
    BE-->>FE: 200 OK (DashboardResponse)
    FE-->>User: 완료율, 집중 시간, 블록 목록 표시
```

---

## SD-006: 토큰 갱신 (자동)

```mermaid
sequenceDiagram
    participant FE as Frontend (Axios Interceptor)
    participant BE as Backend
    participant DB as PostgreSQL

    FE->>BE: API 요청 (만료된 Access Token)
    BE-->>FE: 401 Unauthorized (E002)
    FE->>FE: Axios Interceptor 감지
    FE->>BE: POST /api/v1/auth/refresh (refreshToken)
    BE->>DB: SELECT refresh_tokens WHERE token=? AND expires_at > now()
    alt Refresh Token 만료
        DB-->>BE: 없음
        BE-->>FE: 401 Unauthorized
        FE->>FE: 로그인 페이지로 리다이렉트
    else 유효
        DB-->>BE: 토큰 정보
        BE->>BE: 새 Access Token 발급
        BE-->>FE: 200 OK (새 accessToken)
        FE->>FE: 토큰 교체
        FE->>BE: 원래 API 요청 재시도
        BE-->>FE: 정상 응답
    end
```

---

## SD-007: 세션 회고 작성

```mermaid
sequenceDiagram
    actor User
    participant FE as Frontend
    participant BE as Backend
    participant DB as PostgreSQL

    User->>FE: 회고 팝업에서 평점, 메모 입력
    FE->>BE: POST /api/v1/sessions/{id}/retrospective (JWT)
    BE->>DB: SELECT focus_sessions WHERE id=?
    BE->>BE: 소유권 검증
    BE->>BE: completedAt + 24h > now() 검증
    alt 24시간 초과
        BE-->>FE: 422 Unprocessable Entity (E008)
        FE-->>User: "회고는 완료 후 24시간 이내에만 가능합니다"
    else 가능
        BE->>DB: INSERT INTO retrospectives
        BE-->>FE: 201 Created (RetrospectiveResponse)
        FE-->>User: 회고 저장 완료 표시
    end
```
