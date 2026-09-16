# D-001 File과 FileLocation 분리

Status: Accepted

## Context

파일 자체의 속성과 파일이 저장된 위치는 다른 개념이다.

## Decision

```text
File
- name
- mimeType
- size

FileLocation
- storageSource
- externalId
- path
- modifiedAt
```

## Consequences

- 한 File에 여러 Location을 표현할 수 있다.
- 조회에 Join이 필요하다.
- Cross-storage 동일 콘텐츠 판정은 별도 로직이 필요하다.
