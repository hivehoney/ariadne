# Metadata Sync

## 목적

Provider에서 수집한 파일 정보를 Ariadne 공통 Metadata로 변환해 Backend Domain에 반영한다.

핵심은 Provider 호출과 DB 변경을 하나의 긴 Transaction으로 묶지 않는 것이다.

## 기본 흐름

```text
Provider Metadata 조회
↓
공통 Metadata 변환
↓
Backend 전송
↓
StorageSource 확인
↓
File / FileLocation 반영
↓
lastSyncedAt 갱신
```

## Transaction Boundary

잘못된 구조:

```text
@Transactional
↓
외부 Provider API 호출
↓
수 초 대기
↓
DB 변경
↓
Commit
```

이 경우 외부 API가 느릴 때 Transaction과 DB Connection을 불필요하게 오래 유지할 수 있다.

기본 원칙:

```text
Application / Coordination
- 외부 I/O
- Transaction 없음

Persistence
- DB 변경만 수행
- 짧은 @Transactional
```

## Persistence

Persistence Transaction에서 수행할 수 있는 작업:

- 기존 `(storageSourceId, externalId)` 조회
- File 생성/수정
- FileLocation 생성/수정
- StorageSource `lastSyncedAt` 변경

중간 DB 처리에 실패하면 같은 Persistence Transaction 안의 변경은 Rollback한다.

외부 Provider 조회 성공과 DB Commit까지 하나의 분산 ACID Transaction을 보장하는 것은 아니다.

## 향후 신뢰성

실제 문제를 확인한 뒤 다음을 도입한다.

- Incremental Sync
- Remote Delete 반영
- Retry / Backoff
- Sync Lock
- Batch/Chunk
- Async Job
- Idempotency

Kafka/Redis를 먼저 넣고 문제를 맞추지 않는다.
