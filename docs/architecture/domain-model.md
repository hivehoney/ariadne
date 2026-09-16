# Domain Model

## 모델링 원칙

Ariadne는 File 자체와 File이 존재하는 위치를 분리한다.

```text
File
  1
  │
  N
FileLocation
  N
  │
  1
StorageSource
```

## StorageSource

연결된 하나의 Storage 계정 또는 저장 공간을 나타낸다.

예:

```text
내 Google Drive
회사 OneDrive
Home PC
Android Device
```

StorageSource가 필요한 이유는 FileLocation이 단순한 path 문자열만으로는 어느 Provider/Device의 위치인지 표현할 수 없기 때문이다.

주요 책임:

- Storage 종류
- 사용자 표시 이름
- 연결 단위 식별
- 동기화 상태의 기준점

Provider별 File을 직접 Domain에 넣지 않고 StorageSource라는 공통 연결 단위로 추상화한다.

## File

논리적인 파일 자체를 표현한다.

주요 정보:

- name
- mimeType
- size
- 내부 생성/수정 시각

File은 "어디에 저장되어 있는가"보다 "무슨 파일인가"에 집중한다.

## FileLocation

File이 실제로 존재하는 위치를 표현한다.

주요 정보:

- File
- StorageSource
- Provider externalId
- path
- Provider modifiedAt

반복 동기화의 유일성 기준은 다음 조합이다.

```text
(storageSourceId, externalId)
```

이 값은 동일 Provider 파일을 반복 Sync할 때 중복 Row가 생기는 것을 막는 기준이지, 서로 다른 Storage의 콘텐츠가 동일하다는 뜻은 아니다.

## Cross-storage Deduplication

Schema는 한 File에 여러 FileLocation을 연결할 수 있는 방향을 지원하지만, 동일 콘텐츠 판정은 별도 문제다.

향후 후보:

- Content Hash
- Size
- MIME Type
- Provider checksum

Deduplication 로직이 없는 상태에서 이름만 같다는 이유로 여러 Location을 하나의 File로 합치지 않는다.

## 관계의 Lazy Loading

JPA 관계는 필요 시 Lazy를 사용한다.

Kotlin + JPA에서는 다음을 주의한다.

- Entity를 무조건 `data class`로 만들지 않는다.
- `equals/hashCode/toString`이 Lazy 관계를 의도치 않게 접근하지 않게 한다.
- Hibernate Proxy를 고려한다.
- `optional = false`는 JPA 관계 제약, `nullable = false`는 DB column 제약이라는 차이를 유지한다.

N+1은 Lazy 자체를 없애는 방식보다 조회 요구에 따라 Fetch Join/EntityGraph/Projection으로 해결한다.
