# Roadmap

현재 구현과 미래 계획을 분리한다.

## 현재 기반

- Android Kotlin/Compose
- Provider 독립 Storage UI 구조
- Google Drive 인증/파일 조회
- Room + KSP Metadata Cache
- Offline Metadata 조회
- Kotlin/Spring Boot Backend
- JPA/Hibernate/MySQL
- File / FileLocation / StorageSource Domain
- 외부 I/O와 DB Transaction 분리 원칙

## 다음 우선순위

### 1. Android → Backend Metadata Sync

Provider에서 가져온 공통 Metadata를 Backend에 전달하는 계약을 확정한다.

완료 기준:

- Provider DTO가 Backend Domain으로 직접 노출되지 않음
- 반복 전송이 중복 Row를 만들지 않음
- 오류 계약이 정의됨

### 2. Backend Read/Search Vertical Slice

- StorageSource 목록
- File 목록
- File Detail + Location
- 이름 기반 검색
- Android 검색 화면 연결

초기 검색은 MySQL Query/Index로 시작한다.

### 3. Sync Reliability

실제 장애/규모를 측정한 뒤:

- Incremental Sync
- Remote Delete
- Retry / Backoff
- Idempotency
- Sync 상태
- Batch 처리

### 4. 두 번째 Provider

OneDrive 등 실제 두 번째 Provider를 추가해 `StorageClient`, File/FileLocation 모델과 공통 UI 추상화를 검증한다.

### 5. Organization

- Filename Normalization
- Topic Candidate
- Fragmentation Score
- Virtual Collection
- Organization Suggestion

Rule/Metadata 기반 정확도를 먼저 측정한다.

## 이후 후보

문제가 확인된 경우에만 검토한다.

- Redis
- Kafka
- Elasticsearch
- Windows Agent
- Streaming
- WebFlux/Netty
- Kubernetes
- ArgoCD
- Vault
- Semantic Similarity
- Content Analysis

## 금지

실제 측정이나 사용 사례 없이 포트폴리오 기술 스택을 늘리기 위한 목적으로 Infrastructure를 추가하지 않는다.
