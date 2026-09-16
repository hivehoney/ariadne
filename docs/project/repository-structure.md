# Repository Structure

Ariadne는 하나의 Git Repository 안에서 Android, Backend, Infrastructure와 문서를 함께 관리한다.

```text
repository-root/
├── android/
├── backend/
├── infra/
├── docs/
├── AGENTS.md
├── CLAUDE.md
└── README.md
```

## android/

Android Client 애플리케이션이다.

주요 책임:

- 사용자 UI
- Provider 인증 및 권한 획득
- Google Drive 등 외부 Storage API 접근
- Provider 파일 목록 조회
- 로컬 파일 Metadata Cache
- Storage 화면과 통합 검색 UI
- Backend와 Metadata 동기화

Android와 Backend는 별도 Gradle 프로젝트다.

## backend/

Kotlin + Spring Boot 기반 Backend다.

주요 책임:

- Ariadne 내부 File Domain
- StorageSource와 File/FileLocation Metadata 관리
- 향후 통합 검색, 분석, 정리 기능
- Android가 전달한 Metadata 영속화
- 향후 사용자/기기/동기화 상태 관리

Provider 인증 자체를 Backend의 기본 책임으로 두지 않는다.

## infra/

개발과 운영에 필요한 인프라 정의를 둔다.

현재 주요 범위:

```text
infra/
├── docker-compose.yml
└── mysql/
    └── init/
```

초기에는 MySQL 등 실제로 사용하는 구성만 둔다. Redis, Kafka, Elasticsearch, Kubernetes는 필요성이 확인되기 전까지 추가하지 않는다.

## docs/

제품 방향과 설계, 구현 규칙, 결정 이력을 관리한다.

문서는 코드 설명을 반복하는 장소가 아니라 다음 질문에 답해야 한다.

- 이 컴포넌트는 왜 존재하는가?
- 책임은 어디까지인가?
- 어떤 문제를 피하기 위해 이 구조를 선택했는가?
- 다른 선택지는 무엇이었는가?
- 현재 구현과 미래 계획의 경계는 무엇인가?

## AGENTS.md / CLAUDE.md

프로젝트 문서를 복제하지 않는다.

두 파일에는 AI가 실제 작업할 때 반드시 따라야 하는 짧은 규칙과 문서 진입점만 둔다.

예:

```text
Read first:
- docs/README.md
- docs/architecture/overview.md
- docs/decisions/README.md
```

세부 설계는 `docs/`를 Source of truth로 사용한다.
