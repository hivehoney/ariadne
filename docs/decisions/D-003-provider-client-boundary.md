# D-003 StorageClient 경계

Status: Accepted

## Context

Google Drive SDK/DTO가 공통 UI와 Domain에 퍼지면 두 번째 Provider 추가 시 핵심 코드가 계속 변경된다.

## Decision

```text
StorageClient
├── GoogleDriveStorageClient
├── OneDriveStorageClient
└── MyBoxStorageClient
```

Provider 전용 DTO는 Adapter/Client 내부에서 공통 Model로 변환한다.

## Consequences

- 공통 Storage UI 재사용
- Provider 교체/추가 범위 축소
- Interface/Mapper 코드 증가
- 두 번째 Provider 구현 때 추상화 적합성을 재검증해야 함
