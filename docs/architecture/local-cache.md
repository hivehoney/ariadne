# Android Local Cache

## 역할

Room은 Android에서 Provider Metadata의 로컬 Cache를 관리한다.

SQLite를 직접 다루는 대신 Room을 사용해 Entity/DAO/Database와 Query 검증을 구조화한다.

## KSP

Room Compiler가 Kotlin Source를 분석해 DAO/Database 구현 코드를 생성하기 위해 KSP를 사용한다.

```text
Room Annotation
↓
KSP
↓
Room Compiler
↓
Generated Implementation
↓
Room Runtime
↓
SQLite
```

KSP는 DB 자체가 아니라 컴파일 시 코드 생성 도구다.

## 흐름

```text
Storage 화면 진입
↓
Room 데이터 관찰
↓
Cache 즉시 표시
↓
Provider API 조회
↓
Cache Upsert
↓
UI 자동 갱신
```

## Offline

네트워크가 없어도 마지막 Sync Metadata를 Room에서 읽어 파일 목록을 보여줄 수 있다.

이는 파일 원본을 Offline으로 사용할 수 있다는 의미가 아니다. 저장된 것은 Metadata Cache다.

## UI 업데이트 주의

초기 Cache를 표시한 뒤 Sync 완료 시 목록 전체 순서가 크게 바뀌면 사용자에게 화면이 튀는 느낌을 준다.

따라서 정렬 기준과 Upsert 정책을 명확히 하고, 불필요하게 전체 목록을 재구성하지 않는다.
