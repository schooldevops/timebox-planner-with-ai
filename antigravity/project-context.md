# Project Context: 상품관리 시스템 개발

## Context Management
컨텍스트 관리는 project-context.md 파일에 요건 내용을 차근차근 기록하고, 결과물은 docs 폴더 하위에 요청이름/하위디렉토리에 순서대로 확인할 수 있도록 번호를 매겨서 생성하여 컨텍스트를 관리해야한다.
project-context-[projecdt-name].md 파일 을 만들어 관리해주고, docs/[projecdt-name] 폴더에 결과물을 생성해야한다.

## 프로젝트 정보 형식
- **프로젝트명**: [project-name]
- **요청 부서**: [request-department]
- **시작일**: [start-date]
- **현재 단계**: [current-phase]

## 완료된 산출물 형식
- [x] 요건정의서 (v1.0) - `docs/상품관리/01-requirements/01-requirements-spec.md`
  - 13개 기능/비기능 요구사항 정의
  - 10개 핵심 예외 케이스 식별
  - 20개 리스크 기반 테스트 케이스 작성
- [x] 테스트 케이스 (v1.0) - `docs/상품관리/01-requirements/02-test-cases.md`
  - 상세 테스트 시나리오
  - Kotest/Playwright 자동화 스크립트
  - 테스트 실행 계획 (6일)
- [x] 용어 정의서 (v1.0) - `docs/상품관리/01-requirements/03-glossary.md`
  - 도메인 용어 30개 정의
  - 에러 코드 8개 정의
  - 데이터 타입 명세
- [x] 인터페이스 정의서 (v1.0) - `docs/상품관리/02-analysis/01-interface-spec.md`
  - 12개 API 엔드포인트 정의
  - 요청/응답 스키마 상세
  - 인증/인가 규칙
  - Rate Limiting 정책
- [x] 비즈니스 로직 상세 (v1.0) - `docs/상품관리/02-analysis/02-business-logic-detail.md`
  - 6개 주요 기능 처리 흐름
  - 13개 비즈니스 규칙 (의사코드)
  - 예외 처리 전략
  - 트랜잭션 관리
- [x] 데이터 모델 (v1.0) - `docs/상품관리/02-analysis/03-data-model.md`
  - ERD (7개 엔티티)
  - 엔티티 상세 정의
  - 인덱스 및 제약조건
  - 파티셔닝 전략
- [x] 시퀀스 다이어그램 (v1.0) - `docs/상품관리/02-analysis/04-sequence-diagrams.md`
  - 7개 주요 시나리오
  - 동시성 제어 흐름
  - 캐싱 전략
  - 비동기 처리 흐름
- [x] OpenAPI 스펙 (v1.0) - `docs/상품관리/03-design/01-openapi.yaml`
  - OpenAPI 3.0 표준
  - 12개 엔드포인트 상세
  - 스키마 정의 (Request/Response)
  - 보안 정의 (JWT Bearer)
  - 에러 응답 예시
- [x] 시스템 아키텍처 (v1.0) - `docs/상품관리/03-design/02-architecture.md`
  - C4 모델 다이어그램 (Context, Container, Component)
  - AWS 배포 아키텍처 (ECS, RDS, ElastiCache, S3)
  - 보안 아키텍처 (WAF, JWT, RBAC, Encryption)
  - 확장성 전략 (Auto Scaling, Read Replica)
  - 고가용성 (Multi-AZ, Failover, Backup)
  - 모니터링 메트릭 (Prometheus, Grafana)
  - 성능 최적화 (캐싱, 인덱스, 쿼리)
- [x] MSA 설계 (v1.0) - `docs/상품관리/03-design/03-msa-design.md`
  - 서비스 경계 정의 (6개 서비스)
  - 통신 패턴 (gRPC, Kafka)
  - 데이터 관리 (Database per Service)
  - Saga Pattern (재고 차감)
  - CQRS Pattern (조회 최적화)
  - API Gateway 설계 (Spring Cloud Gateway)
  - Circuit Breaker (Resilience4j)
  - Service Mesh (Istio)
- [x] 기술 스택 (v1.0) - `docs/상품관리/03-design/04-tech-stack.md`
  - Backend: Spring Boot 3.2, Kotlin 1.9
  - Frontend: Next.js 15, React 19
  - Database: PostgreSQL 15, Redis 7, Elasticsearch 8
  - Messaging: Kafka 3.6
  - Infrastructure: Kubernetes 1.28, Istio 1.20
  - Observability: Prometheus, Grafana, Jaeger
  - CI/CD: GitHub Actions, ArgoCD
  - 개발 환경 설정 (Docker Compose)

## 주요 요구사항 요약

### 기능 요구사항 

### 비기능 요구사항

### 핵심 예외 케이스

## 기술 스택

## 제약사항

## 산출물 디렉토리 구조