# Architecture Decisions

장기 영향을 주는 결정은 ADR로 관리한다.

## 현재 결정

| ID | 결정 | 상태 |
|---|---|---|
| D-001 | File과 FileLocation 분리 | Accepted |
| D-002 | 외부 I/O와 DB Transaction 분리 | Accepted |
| D-003 | Provider별 구현을 StorageClient 경계로 격리 | Accepted |
| D-004 | Android가 외부 Storage 인증/권한을 담당 | Accepted |
| D-005 | Backend는 Provider Token을 기본 저장하지 않음 | Accepted |
| D-006 | Android Provider Metadata를 Room에 Cache | Accepted |
| D-007 | 실제 이동보다 Virtual Collection 우선 | Accepted |
| D-008 | Rule/Metadata 분석 후 필요할 때 Semantic Analysis 도입 | Accepted |
| D-009 | Backend Provider Refresh Token 저장/AES-GCM 기본 구조 | Superseded by D-004/D-005 |

결정을 변경할 때 기존 문서를 조용히 덮어쓰지 않는다.

새 Decision이 이전 Decision을 대체하면 `Superseded` 관계를 명시한다.
