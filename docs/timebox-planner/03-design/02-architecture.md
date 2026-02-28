# 시스템 아키텍처: Timebox Planner

**버전**: v1.0 | **작성일**: 2026-02-28 | **작성자**: Interface Agent (설계 Agent)

---

## 1. 전체 시스템 구성도 (C4 Model)

### Level 1 – Context Diagram

```mermaid
graph TB
    User["👤 사용자<br/>(웹 브라우저)"]
    System["Timebox Planner<br/>시스템"]
    PushAPI["🔔 Web Push API<br/>(브라우저 내장)"]

    User -->|"HTTPS 요청"| System
    System -->|"알림 트리거"| PushAPI
    PushAPI -->|"데스크탑 알림"| User
```

### Level 2 – Container Diagram

```mermaid
graph TB
    subgraph Browser["브라우저 (클라이언트)"]
        FE["Next.js 15<br/>App Router<br/>Tailwind CSS v4"]
    end

    subgraph Backend["백엔드 서버"]
        API["Spring Boot 3.x<br/>REST API Server<br/>Kotlin"]
    end

    subgraph Infra["인프라 (Docker Compose)"]
        PG["PostgreSQL 16<br/>Primary DB"]
        Redis["Redis 7<br/>캐시 / 세션"]
    end

    FE -->|"REST API HTTPS"| API
    API -->|"jOOQ 쿼리"| PG
    API -->|"캐시 조회/저장"| Redis
```

### Level 3 – Component Diagram (Backend)

```mermaid
graph TB
    subgraph Presentation["Presentation Layer"]
        AC["AuthController"]
        TC["TaskController"]
        TBC["TimeboxController"]
        SC["SessionController"]
        DC["DashboardController"]
        TagC["TagController"]
    end

    subgraph Application["Application Layer"]
        AS["AuthService"]
        TS["TaskService"]
        TBS["TimeboxService"]
        SS["SessionService"]
        DS["DashboardService"]
        TagS["TagService"]
    end

    subgraph Domain["Domain Layer"]
        User["User"]
        Task["Task"]
        Timebox["Timebox"]
        Session["FocusSession"]
        Retro["Retrospective"]
        Tag["Tag"]
    end

    subgraph Infra2["Infrastructure Layer"]
        UR["UserRepository<br/>(jOOQ)"]
        TR["TaskRepository"]
        TBR["TimeboxRepository"]
        SR["SessionRepository"]
        TagR["TagRepository"]
        JWT["JwtTokenProvider"]
        Cache["RedisCache"]
    end

    AC --> AS
    TC --> TS
    TBC --> TBS
    SC --> SS
    DC --> DS
    TagC --> TagS

    AS --> User
    TS --> Task
    TBS --> Timebox
    SS --> Session

    AS --> UR
    AS --> JWT
    TS --> TR
    TBS --> TBR
    SS --> SR
    DS --> Cache
    TagS --> TagR
```

---

## 2. 레이어 아키텍처

```
┌─────────────────────────────────────────────┐
│         Presentation Layer                   │
│  @RestController, @RequestMapping            │
│  DTO 변환, 요청/응답 직렬화, 예외 처리           │
├─────────────────────────────────────────────┤
│         Application Layer                    │
│  @Service, Use Case 구현                     │
│  트랜잭션 경계(@Transactional), 비즈니스 흐름    │
├─────────────────────────────────────────────┤
│         Domain Layer                         │
│  도메인 엔티티, 값 객체, 도메인 서비스             │
│  비즈니스 규칙 캡슐화                           │
├─────────────────────────────────────────────┤
│         Infrastructure Layer                 │
│  jOOQ Repository 구현, JWT, Redis, 이메일      │
│  외부 시스템 연동                               │
└─────────────────────────────────────────────┘
```

---

## 3. 배포 아키텍처 (Docker Compose - 로컬)

```mermaid
graph LR
    subgraph Docker["Docker Compose Network"]
        FE_C["frontend<br/>Next.js<br/>:3000"]
        BE_C["backend<br/>Spring Boot<br/>:8080"]
        PG_C["postgres<br/>PostgreSQL 16<br/>:5432"]
        Redis_C["redis<br/>Redis 7<br/>:6379"]
    end

    Browser["브라우저"] -->|":3000"| FE_C
    FE_C -->|"API 호출 :8080"| BE_C
    BE_C --> PG_C
    BE_C --> Redis_C
```

---

## 4. 보안 아키텍처

```
요청 흐름:
Client → [HTTPS] → Spring Security Filter Chain → Controller

Spring Security Filter Chain:
1. JwtAuthenticationFilter     - Authorization 헤더에서 JWT 추출 및 검증
2. UsernamePasswordAuthFilter  - 로그인 처리 (비활성화, JWT 방식)
3. SecurityContextHolder       - 인증 정보 저장

JWT 전략:
- Access Token:  1시간, 메모리/쿠키 저장
- Refresh Token: 7일, HttpOnly 쿠키 또는 DB 저장
- 서명 알고리�m: HS512

RBAC:
- ROLE_USER: 모든 자원 본인 소유 기준 CRUD
- 소유권 검사: Service 계층에서 userId 비교
```

---

## 5. 데이터 흐름도

```mermaid
sequenceDiagram
    participant FE as Frontend
    participant Filter as JwtFilter
    participant Controller
    participant Service
    participant Cache as Redis
    participant DB as PostgreSQL

    FE->>Filter: HTTP 요청 + Bearer Token
    Filter->>Filter: JWT 서명 검증
    Filter->>Controller: SecurityContext 설정 후 전달
    Controller->>Service: DTO → Domain 변환
    Service->>Cache: 캐시 조회 (대시보드 등)
    alt 캐시 HIT
        Cache-->>Service: 캐시 데이터 반환
    else 캐시 MISS
        Service->>DB: jOOQ 쿼리
        DB-->>Service: 결과
        Service->>Cache: 캐시 저장 (TTL 5분)
    end
    Service-->>Controller: 결과 반환
    Controller-->>FE: JSON 응답
```
