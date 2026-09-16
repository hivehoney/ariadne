# Kotlin + JPA

## Entity

Kotlin Entity는 일반 애플리케이션 Model과 다르게 다룬다.

주의:

- `data class`를 기본 선택으로 사용하지 않는다.
- Hibernate Proxy를 고려한다.
- 자동 생성 `equals/hashCode/toString`이 Lazy 관계를 순회하지 않게 한다.
- 기본 생성자/Proxy 요구를 Gradle JPA Plugin 설정과 함께 관리한다.

## Lazy Loading

Lazy Loading 문제의 핵심은 Kotlin만의 문제가 아니다.

N+1 해결 후보:

- Fetch Join
- `EntityGraph`
- Projection
- 전용 Query

모든 관계를 EAGER로 바꾸는 것은 해결책이 아니다.

## optional과 nullable

```kotlin
@ManyToOne(
    fetch = FetchType.LAZY,
    optional = false,
)

@JoinColumn(
    nullable = false,
)
```

두 설정은 같은 의미가 아니다.

- `optional = false`: JPA 관계상 반드시 존재
- `nullable = false`: DB Column에 NULL 금지

Domain/JPA 제약과 DB 제약을 둘 다 명시할 수 있다.

## Dirty Checking

Transactional Persistence Context에서 Managed Entity를 수정하면 Commit 시 Hibernate Dirty Checking이 UPDATE를 수행한다.

따라서 Entity Update Method는 어떤 필드를 변경하는지 명확하게 유지한다.
