# D-002 외부 I/O와 DB Transaction 분리

Status: Accepted

## Context

Provider API가 느릴 때 DB Transaction까지 유지하면 Connection Pool 자원을 불필요하게 점유한다.

## Decision

```text
외부 I/O
@Transactional 없음

DB Persistence
짧은 @Transactional
```

## Consequences

- 외부 API 대기 중 DB Connection을 잡지 않는다.
- DB 변경 단위의 원자성은 보장한다.
- 외부 호출 성공과 DB Commit은 하나의 ACID Transaction이 아니다.
