# Product Vision

## 한 줄 정의

Ariadne는 Google Drive, OneDrive, Windows, Android 등 여러 Storage와 Device에 분산된 파일을 하나의 File 중심 경험으로 다루고, 관련 파일의 파편화를 발견해 정리를 돕는 개인 파일 검색·정리 서비스다.

## 해결하려는 문제

기존 File Manager는 주로 다음 질문을 해결한다.

```text
어느 Storage에 어떤 File이 있는가?
```

Ariadne는 다음 질문까지 해결하는 것을 목표로 한다.

```text
내가 찾는 File은 어디에 있는가?
+
같은 일을 위해 만든 File들이 어디에 흩어져 있는가?
+
어떤 File들을 함께 관리하면 좋은가?
```

핵심 제품 흐름:

```text
Search
↓
Discover Fragmentation
↓
Organize
```

## File 중심 모델

사용자가 저장소 위치를 먼저 기억해야 하는 UX를 줄인다.

```text
Storage 중심
Google Drive → Folder → File

File 중심
Search → File → Location
```

Storage 위치는 중요한 정보지만 사용자 경험의 중심 객체는 File이다.

## 제품 원칙

- 여러 Storage를 하나의 화면에 나열하는 것만으로 완료하지 않는다.
- 자동 정리는 보수적으로 수행한다.
- 원본 파일을 사용자의 명시적 승인 없이 이동하지 않는다.
- 정리 기능은 실제 이동보다 Virtual Collection을 우선한다.
- AI/LLM은 제품 정체성이 아니라 필요할 때 사용하는 구현 수단이다.
- Rule 기반 방식의 한계를 측정하기 전 Semantic/LLM 분석을 먼저 도입하지 않는다.
