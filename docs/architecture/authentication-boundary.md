# Authentication Boundary

## 현재 결정

외부 Storage의 인증과 권한 획득은 Android에서 처리한다.

```text
Android
  ↓
Provider Authorization
  ↓
Provider API

Backend
  ↓
Ariadne Metadata
```

모바일 앱은 Public Client이므로 Provider가 지원하는 Authorization Code + PKCE 계열 방식을 우선한다.

## Backend에 Provider Token을 기본 저장하지 않는 이유

Ariadne Backend의 핵심 책임은 여러 Provider의 인증 세션을 중앙 보관하는 것이 아니라 통합 Metadata와 검색/정리 Domain을 관리하는 것이다.

Provider Token 저장을 기본 구조로 두면:

- Provider별 Token lifecycle이 Backend 책임으로 이동
- Refresh/Revocation 정책이 서버에 누적
- 민감 Credential 저장 범위 증가
- Android에서도 필요한 인증 책임과 중복 가능

따라서 현재 방향에서는 Provider Token을 Backend의 기본 영속 모델로 두지 않는다.

## 새 기기

새 기기에서는 Ariadne 로그인 후 Provider별 Silent Authorization을 먼저 시도한다.

```text
성공
→ 바로 사용

실패
→ 해당 Provider의 동의/로그인 화면
```

Ariadne 계정 연결 정보와 Provider 인증 세션 자체는 같은 개념으로 취급하지 않는다.

## 과거 Backend Credential 설계

초기 Backend 설계에는 다음 구조가 존재했다.

```text
StorageSource
1:1
StorageCredential

Refresh Token
→ JSON
→ AES-GCM
→ DB 저장
```

AES-GCM 설계 자체는 복호화가 필요한 Secret 저장이라는 요구에는 타당했지만, 현재 인증 책임 경계 변경으로 Provider Token을 Backend에 저장하는 기본 경로는 보류한다.

기존 코드가 남아 있다면 새 기능이 이를 자동으로 확장해서는 안 된다. 제거/유지 여부는 별도 리팩터링 작업과 Decision에서 처리한다.
