# AI Development Rules

이 문서는 Claude Code와 Codex가 Ariadne에서 코드를 변경할 때 따라야 할 작업 규칙을 정의한다.

## 작업 전

1. 저장소 구조를 확인한다.
2. 현재 Branch와 Worktree를 확인한다.
3. `docs/architecture/overview.md`를 읽는다.
4. 작업 대상에 대응하는 상세 문서를 읽는다.
5. 관련 `decisions/`를 확인한다.

## 코드 변경 원칙

새 요소를 추가할 때 다음 네 가지를 먼저 판단한다.

1. 역할
2. 지금 필요한 이유
3. 대안 또는 지금 도입하지 않아도 되는지
4. 적용 위치

새 기술을 사용하고 싶다는 이유만으로 Dependency나 Infrastructure를 추가하지 않는다.

## Android / Backend 경계

기본 원칙:

```text
Android
- Provider 인증
- Provider 권한
- Provider API 직접 호출
- 로컬 Cache
- 사용자 UI

Backend
- Ariadne 내부 Metadata
- 통합 File Domain
- 검색/분석/정리
- 장기 상태
```

Android가 Google Drive를 지원하기 위해 Backend에 Google 전용 인증 코드를 다시 만드는 식의 중복 구현을 피한다.

## Provider 경계

Provider별 구현은 Adapter/Client 경계 안에 둔다.

```text
StorageClient
├── GoogleDriveStorageClient
├── OneDriveStorageClient
└── MyBoxStorageClient
```

공통 UI와 Domain은 Provider SDK/DTO를 직접 알지 않는다.

## 문서 동기화

다음 변경은 문서를 함께 수정한다.

- Entity/DDL 변경 → `architecture/domain-model.md`, `reference/database.md`
- 인증/Provider 책임 변경 → `architecture/authentication-boundary.md`
- 큰 구조 변경 → `architecture/overview.md`
- 새 장기 선택 → `decisions/`
- 개발 순서 변경 → `roadmap.md`
- Git 정책 변경 → `project/git-workflow.md`

## 완료 시

- Build/Test 결과 확인
- 아직 지원하지 않는 범위 확인
- Commit 시점이면 적절한 Commit Message 제안
- 설계 결정이 생겼으면 문서 반영
