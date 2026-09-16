# Credential Security

이 문서는 과거/보조 설계에서 Backend가 복호화 가능한 Provider Credential을 보관해야 할 경우의 보안 원칙을 기록한다.

현재 기본 아키텍처는 Provider 인증을 Android에서 처리하고 Backend에 Provider Token을 저장하지 않는 방향이다.

## AES-GCM

복호화가 필요한 Secret을 DB에 저장해야 하는 경우 Hash는 사용할 수 없다.

AES-GCM은 다음을 동시에 제공한다.

- 기밀성
- 무결성/변조 검증

기본 형식 예:

```text
version
+
random IV
+
ciphertext
+
authentication tag
```

같은 Key/IV 조합을 재사용하면 안 되므로 암호화마다 새 IV를 만든다.

## Key Management

알고리즘보다 Key 관리가 더 중요하다.

금지:

```text
Git에 실제 Key Commit
application.yml에 운영 Key 고정
로그에 Token 출력
```

운영에서 Credential 저장이 다시 필요해지면 KMS/Vault 또는 Envelope Encryption을 검토한다.

## 현재 상태

이 문서는 Provider Token의 Backend 저장을 권장하는 문서가 아니다.

과거 StorageCredential/AES-GCM 코드가 존재하는 경우 그 설계의 보안 전제와 제거/재사용 판단을 위한 참고 문서다.
