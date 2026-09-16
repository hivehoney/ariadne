# Architecture Overview

## 현재 목표 구조

Ariadne는 하나의 Repository 안에서 Android와 Backend를 분리된 실행 애플리케이션으로 개발한다.

```text
Google Drive / OneDrive / ...
            ▲
            │ Provider API
            │
        Android App
        ├─ Authorization
        ├─ StorageClient
        ├─ Room Cache
        └─ Storage UI
            │
            │ Metadata Sync
            ▼
      Spring Boot Backend
        ├─ File Domain
        ├─ StorageSource
        ├─ FileLocation
        ├─ Search
        └─ Organization
            │
            ▼
          MySQL
```

## 핵심 경계

### Android

Android는 사용자와 Provider에 가장 가까운 계층이다.

책임:

- 모바일 Public Client 기반 인증/권한
- Authorization Code + PKCE 등 Provider 권장 방식 사용
- Provider API 직접 호출
- 파일 목록/용량 등 Provider 정보 조회
- Room 기반 로컬 Metadata Cache
- Offline 조회
- 공통 Storage UI

### Backend

Backend는 특정 Provider SDK가 아니라 Ariadne의 내부 File Domain을 관리한다.

책임:

- StorageSource
- File
- FileLocation
- 통합 Metadata
- 향후 Search
- Topic/Fragmentation 분석
- Organization Suggestion

Provider Token을 Backend의 기본 영속 상태로 두지 않는다.

## Provider 독립 구조

UI와 공통 흐름:

```text
StorageRoute
↓
StorageViewModel
↓
StorageClient
├── GoogleDriveStorageClient
├── OneDriveStorageClient
└── MyBoxStorageClient
```

Google Drive만의 인증 UI는 전용 Route에서 처리하고, 연결 이후 파일 화면은 공통 Storage 구조를 사용한다.

## 로컬 Cache

Provider API 결과는 Room에 저장해 네트워크가 없을 때도 이전 Metadata를 조회할 수 있게 한다.

기본 흐름:

```text
Storage 진입
↓
Room Cache 즉시 표시
↓
Provider API 동기화
↓
Room 갱신
↓
UI 최신 상태 반영
```

Cache는 원본 Provider의 Source of truth를 대체하지 않는다.

## Backend Transaction 원칙

외부 I/O와 DB Transaction을 분리한다.

```text
외부 API 호출
@Transactional 없음
↓
필요한 Metadata 확보
↓
Persistence Service
@Transactional
↓
짧은 DB 변경
```

외부 API 지연 동안 DB Connection을 점유하지 않기 위한 원칙이다.

## 현재 의도적으로 도입하지 않는 것

아래 기술은 문제와 측정 결과가 생기기 전에는 추가하지 않는다.

- Redis
- Kafka
- Elasticsearch
- WebFlux/Netty Relay
- Kubernetes
- ArgoCD
- Vault
- AI/LLM 자동 분류

기술 스택 자체가 목표가 아니라 실제 문제를 해결하기 위한 수단이다.
