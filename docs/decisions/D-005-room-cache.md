# D-005 Room Metadata Cache

Status: Accepted

## Context

Storage 화면이 Provider Network 응답에만 의존하면 진입 지연과 Offline 사용 문제가 생긴다.

## Decision

Provider Metadata를 Android Room DB에 Cache한다.

```text
Room Cache 표시
↓
Provider Sync
↓
Room Update
↓
UI 반영
```

## Consequences

- Offline에서도 마지막 Metadata 조회 가능
- 화면 초기 응답성 개선
- Cache invalidation과 정렬 안정성 관리 필요
- File 원본 자체의 Offline 보관을 의미하지 않음
