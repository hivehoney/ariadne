# Testing Strategy

## 원칙

외부 Provider 기능은 다음을 구분한다.

```text
Unit / Adapter Test
Integration Test
Limited Real Provider E2E
```

Mock 통과를 실제 Google 계정 E2E 성공으로 표현하지 않는다.

## Backend

주요 대상:

- Domain 규칙
- Repository 제약
- Persistence Transaction
- Metadata Mapper
- API Contract
- 외부 Provider Adapter Error Mapping

통합 테스트가 개인 개발 DB 데이터를 삭제하거나 기존 상태에 의존하지 않게 한다.

Backend 통합 테스트는 개발용 MySQL과 분리된 Testcontainers 기반 MySQL 8.4를 사용한다.

```text
./gradlew test
    ↓
Testcontainers
    ↓
MySQL 8.4
    ↓
Flyway Migration
    ↓
Hibernate validate
    ↓
Spring / JUnit Test
```

### Test Database

테스트 DataSource는 `test` Profile에서 Testcontainers JDBC URL을 사용한다.

```yaml
spring:
  datasource:
    url: jdbc:tc:mysql:8.4:///ariadne
    username: test
    password: test

  jpa:
    hibernate:
      ddl-auto: validate
```

각 구성의 책임은 다음과 같이 분리한다.

- Testcontainers: 테스트용 MySQL 실행
- Flyway: Schema 생성 및 Migration 관리
- Hibernate: Entity와 Schema Mapping 검증
- Spring/JUnit: 기능 및 통합 테스트

Backend 테스트를 위해 개발용 Docker Compose MySQL을 직접 실행하지 않는다.

로컬 테스트에서는 Testcontainers가 Container를 생성할 수 있도록 Docker Engine이 실행 중이어야 한다.

### Flyway Migration

Database Schema 변경은 Flyway Migration으로 관리한다.

Migration 파일은 다음 위치에 둔다.

```text
backend/src/main/resources/db/migration/
├─ V1__schema.sql
└─ V2__storage_credentials.sql
```

Hibernate는 Schema 생성이나 변경을 담당하지 않는다.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

`validate`를 사용해 Flyway가 구성한 Schema와 JPA Entity Mapping이 일치하는지 검증한다.

### Test Profile

모든 Backend Test는 `test` Profile을 기본으로 사용한다.

```kotlin
tasks.withType<Test> {
    useJUnitPlatform()

    systemProperty(
        "spring.profiles.active",
        "test",
    )
}
```

개발자의 로컬 DB나 실제 외부 Provider에 테스트가 의존하지 않도록 테스트 환경을 통일한다.

## Android

주요 대상:

- StorageViewModel 상태
- Room DAO
- Cache → Sync → Update 흐름
- Provider Client Mapper
- Navigation 핵심 경로
- Offline Cache 조회

Android 변경은 로컬에서 다음 기본 검증을 수행한다.

```text
./gradlew test
./gradlew assembleDebug
./gradlew lintDebug
```

`local.properties`는 개발자별 Android SDK 경로를 포함하므로 Git에 Commit하지 않는다.

Android Studio에서 실행되는 것뿐 아니라 Gradle CLI에서도 Build/Test가 가능한 상태를 유지한다.

## CI

Pull Request와 `develop` Branch 변경은 GitHub Actions에서 자동 검증한다.

Workflow는 다음 위치에서 관리한다.

```text
.github/workflows/ci.yml
```

현재 기본 검증 범위는 다음과 같다.

```text
Backend
├─ build
│  └─ test
└─ ktlintCheck

Android
├─ test
├─ assembleDebug
└─ lintDebug
```

### Backend CI

Backend CI는 별도의 MySQL Service를 구성하지 않는다.

Test 실행 시 Testcontainers가 MySQL 8.4 Container를 생성하고 Flyway가 Schema Migration을 적용한다.

```text
GitHub Actions
    ↓
Backend Build / Test
    ↓
Testcontainers
    ↓
MySQL 8.4
    ↓
Flyway
    ↓
Hibernate validate
```

이를 통해 로컬 테스트와 CI 테스트가 동일한 DB 구성 방식을 사용한다.

### Android CI

Android CI는 개발자의 `local.properties`에 의존하지 않는다.

GitHub Actions Runner에서 JDK와 Android SDK를 구성한 뒤 다음 검증을 수행한다.

```text
test
assembleDebug
lintDebug
```

개발자 PC의 Android SDK 설치 경로가 CI 환경에 영향을 주지 않게 한다.

## 외부 Provider

실제 Provider E2E는 제한된 개발 계정으로 수행한다.

Token/Secret을 Test Log나 Fixture에 저장하지 않는다.
