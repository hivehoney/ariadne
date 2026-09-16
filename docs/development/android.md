# Android Development

## 기본 구조

Compose UI는 Provider별 화면을 복제하지 않고 공통 Storage 화면을 사용한다.

```text
GoogleDriveRoute
  └─ 인증 처리
      ↓
StorageRoute
  ↓
StorageScreen
  ↓
StorageViewModel
```

## Provider 추상화

파일 조회는 `StorageClient` 계층으로 분리한다.

Google 전용 API 호출 코드를 Composable이나 공통 ViewModel에 직접 넣지 않는다.

## Network

Backend 호출은 별도 API Client 계층에서 관리한다.

Base URL은 BuildConfig 등 환경별 설정으로 둔다.

Emulator에서 개발 PC의 localhost는 `10.0.2.2`를 사용할 수 있지만 물리 Device에서는 동일하게 동작하지 않으므로 실행 환경을 구분한다.

## Room

Provider Metadata는 Room에 Cache해 화면 초기 표시와 Offline 조회에 사용한다.

Room Entity는 Backend JPA Entity와 같은 객체로 공유하지 않는다. 각 계층의 목적이 다르다.

## 검증

Android 변경은 Commit 전 다음 기본 검증을 수행한다.

```text
./gradlew test
./gradlew assembleDebug
./gradlew lintDebug
```

Android Studio에서 정상 실행되는 것뿐 아니라 Gradle CLI에서도 Build/Test가 가능한 상태를 유지한다.

`local.properties`는 개발자별 Android SDK 경로를 포함하므로 Git에 Commit하지 않는다.

CI에서는 GitHub Actions Runner의 Android SDK 환경을 사용하며 개발자의 로컬 SDK 경로에 의존하지 않는다.

## 주석

새 Class에는 책임과 범위를 설명하는 주석을 둔다.

Function/핵심 Field는 코드만으로 의도가 명확하지 않은 경우 역할과 이유를 짧게 적는다.

주석으로 코드 자체를 번역하지 않는다.
