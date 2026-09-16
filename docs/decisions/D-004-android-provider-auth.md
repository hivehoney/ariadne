# D-004 Android가 Provider 인증을 담당

Status: Accepted

## Context

Google Drive, OneDrive 등 모바일 Storage 접근은 실제 사용 Device의 사용자 권한과 직접 연결된다.

Backend에서 모든 Provider Token lifecycle을 관리하면 서버 책임과 보안 범위가 커지고 Android 인증 흐름과 중복된다.

## Decision

- Android에서 Provider Authorization 수행
- 모바일 Public Client 방식 사용
- 가능한 경우 Authorization Code + PKCE 사용
- Android가 Provider API 직접 호출
- Backend에는 Ariadne Metadata를 전달

## Consequences

- Provider Token을 Backend의 기본 저장 상태로 두지 않는다.
- Provider 인증 실패/재동의 UX는 Android 책임이 된다.
- 새 기기에서 Silent Authorization 실패 시 Provider 동의를 다시 받을 수 있다.
- 과거 Backend OAuth/StorageCredential 경로는 기본 구조에서 제외한다.
