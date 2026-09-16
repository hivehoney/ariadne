# Storage Integration

## 목표

Google Drive, OneDrive, MyBox 등 Provider가 추가되어도 공통 UI와 Domain이 Provider SDK에 종속되지 않게 한다.

## Android StorageClient

```text
StorageClient
├── GoogleDriveStorageClient
├── OneDriveStorageClient
└── MyBoxStorageClient
```

StorageClient는 공통 Storage 화면이 필요한 파일 조회 동작을 제공한다.

Provider SDK/DTO는 각 구현 내부에 둔다.

## 인증과 파일 조회 분리

Provider 인증은 공통 Storage UI와 같은 책임이 아니다.

예:

```text
GoogleDriveRoute
├── Google Authorization
└── 인증 완료 후 StorageRoute로 위임
```

연결 이후:

```text
StorageRoute
↓
StorageScreen
↓
StorageViewModel
↓
GoogleDriveStorageClient
```

이렇게 구성하면 Provider별 인증 차이가 전체 파일 화면으로 퍼지지 않는다.

## Metadata 변환

Provider 모델을 그대로 Backend Domain에 전달하지 않는다.

```text
Google Drive DTO
↓
Android Provider Mapper
↓
Ariadne Metadata DTO
↓
Backend
↓
File / FileLocation
```

Provider 전용 필드가 필요하면 공통 Domain column을 무분별하게 늘리지 않고 실제 공통성이 확인된 뒤 모델을 확장한다.

## Cache

Provider에서 가져온 Metadata는 Room Cache에 저장한다.

목표:

- 화면 진입 시 즉시 이전 목록 표시
- Offline 조회
- Provider API 지연과 UI 분리

주의:

- Cache는 Provider의 최종 Source of truth가 아니다.
- 새 Sync 결과가 기존 Cache와 합쳐질 때 리스트가 불필요하게 튀지 않도록 정렬/업데이트 정책을 유지한다.
