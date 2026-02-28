# 비즈니스 로직 상세: Timebox Planner

**버전**: v1.0 | **작성일**: 2026-02-28 | **작성자**: Logic Agent

---

## BL-001: 회원 가입 (FR-001)

### 처리 흐름
1. 이메일 형식 검증 (`jakarta.validation @Email`)
2. 비밀번호 형식 검증 (8자 이상, 영문+숫자+특수문자)
3. 이메일 중복 확인 → 존재하면 E001(409) 반환
4. 비밀번호 BCrypt 암호화
5. `users` 테이블에 저장
6. 응답 반환 (비밀번호 필드 제외)

### 비즈니스 규칙
- 이메일: RFC 5321 형식 준수, 대소문자 구분 없이 중복 처리 (소문자로 정규화 후 저장)
- 비밀번호: `^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$`
- 회원 가입 후 자동 로그인 없음 (별도 로그인 API 호출 필요)

### 예외 처리
| 예외 상황 | 에러 코드 | HTTP | 처리 방식 |
|---------|---------|------|---------|
| 이메일 중복 | E001 | 409 | ConflictException 반환 |
| 비밀번호 형식 오류 | E010 | 400 | ValidationException 반환 |
| 이메일 형식 오류 | - | 400 | ConstraintViolationException |

---

## BL-002: 로그인 및 JWT 발급 (FR-001)

### 처리 흐름
1. 이메일로 사용자 조회 → 없으면 E002(401) 반환
2. BCrypt 비밀번호 비교 → 불일치 시 E002(401) 반환
3. Access Token 발급 (유효시간: 1시간)
4. Refresh Token 발급 (유효시간: 7일) → `refresh_tokens` 테이블에 저장
5. 응답 반환

### 비즈니스 규칙
- 로그인 실패 시 "이메일 또는 비밀번호가 올바르지 않습니다" (보안: 어느 쪽이 틀렸는지 미공개)
- Refresh Token은 사용자당 최대 5개 유지 (디바이스 관리)
- 로그아웃 시 해당 Refresh Token DB에서 삭제 (블랙리스트 방식)

### 트랜잭션 경계
- Refresh Token 저장: 단일 트랜잭션

---

## BL-003: 할 일(Task) 생성 (FR-002)

### 처리 흐름
1. 입력값 검증 (제목 필수, 예상 시간 5분 단위)
2. 태그 ID 존재 여부 및 소유권 확인 (최대 5개)
3. Task 생성 및 저장
4. Task-Tag 연관 관계 저장
5. 생성된 Task 반환

### 비즈니스 규칙
- `estimatedMinutes`: 5의 배수만 허용 → `estimatedMinutes % 5 == 0`
- 상태 초기값: `TODO`
- 태그 소유권: 로그인 사용자 소유 태그만 연결 가능

---

## BL-004: Timebox 블록 생성 (FR-003)

### 처리 흐름
1. 날짜/시간 형식 검증
2. `startTime < endTime` 검증
3. 시간 단위 검증: 30분 단위 (`startTime.minute in [0, 30]`)
4. 해당 날짜 사용자의 기존 Timebox와 시간 중복 검사
   ```
   NOT (endTime <= existing.startTime OR startTime >= existing.endTime)
   ```
5. 하루 최대 블록 수 검사 (16개)
6. Task 소유권 검사 (taskId 있는 경우)
7. Timebox 저장

### 비즈니스 규칙
- 시간 중복 조건: `new.startTime < existing.endTime AND new.endTime > existing.startTime`
- 최소 블록 시간: 30분
- 최대 블록 시간: 8시간 (480분)
- 블록 status 초기값: `PLANNED`

### 트랜잭션 경계
- Timebox 생성 + 상태 업데이트: 단일 트랜잭션

---

## BL-005: 집중 타이머 시작 (FR-004)

### 처리 흐름
1. Timebox 소유권 검증
2. Timebox 상태 확인 (`PLANNED` 또는 `PAUSED`만 시작 가능)
3. 현재 사용자의 `RUNNING` 세션 존재 여부 확인 → 있으면 E005(409)
4. Focus Session 생성 (`status: RUNNING`, `startedAt: now()`)
5. Timebox 상태 `RUNNING` 으로 변경
6. 연결된 Task 상태 `IN_PROGRESS` 로 변경

### 비즈니스 규칙
- 한 사용자당 동시 실행 타이머: 1개
- Session 생성 시 `plannedMinutes = timebox.endTime - timebox.startTime`
- 타이머 완료: 서버는 세션 상태만 관리, 실제 알림은 프론트엔드 JavaScript 타이머 처리

### 트랜잭션 경계
- Session 생성 + Timebox 상태 + Task 상태 동시 업데이트: 단일 트랜잭션

---

## BL-006: 타이머 완료 처리 (FR-004)

### 처리 흐름
1. Session 소유권 검증
2. Session 상태 확인 (`RUNNING` 또는 `PAUSED`만 완료 가능)
3. `focusedMinutes` 계산:
   ```
   focusedMinutes = (completedAt - startedAt) - totalPausedMinutes
   ```
4. Session 상태 `COMPLETED`, `completedAt = now()` 저장
5. Timebox 상태 `COMPLETED` 변경
6. `confirmDone: true` 인 경우 Task 상태 `DONE` 변경
7. 응답 반환

### 트랜잭션 경계
- Session + Timebox + (조건부) Task 상태 변경: 단일 트랜잭션

---

## BL-007: 대시보드 집계 (FR-005)

### 일별 대시보드
```
completionRate = (COMPLETED 블록 수 / 전체 블록 수) * 100.0
plannedMinutes = Σ (timebox.endTime - timebox.startTime)
focusedMinutes = Σ session.focusedMinutes (완료된 세션)
```

### 주별 대시보드
```
period: startDate ~ startDate + 6일
dailyStats: 각 일자별 (date, focusedMinutes, completedBlocks)
topTags: 완료된 Task의 태그별 사용 횟수 TOP 5
```

---

## BL-008: 세션 회고 작성 (FR-007)

### 처리 흐름
1. Session 소유권 검증
2. Session 상태 `COMPLETED` 확인
3. `completedAt + 24시간 > now()` 확인 → 초과 시 E008(422)
4. 기존 회고 존재 여부 → 있으면 수정, 없으면 생성
5. 응답 반환
