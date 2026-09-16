# Ariadne Documentation

이 디렉터리는 Ariadne의 제품 방향, 현재 아키텍처, 개발 규칙, 기술 결정과 향후 계획을 관리한다.

Claude Code와 Codex는 작업을 시작하기 전에 최소한 다음 순서로 문서를 읽는다.

1. `project/repository-structure.md`
2. `product/vision.md`
3. `architecture/overview.md`
4. 작업 영역에 해당하는 세부 문서
5. `decisions/README.md`
6. `roadmap.md`

## 문서 구분

- `project/`: 저장소 구조, Git/Worktree, AI 개발 규칙
- `product/`: 제품이 해결하려는 문제와 파일 관리 원칙
- `architecture/`: 현재 시스템 경계와 도메인/데이터 흐름
- `development/`: 구현 시 지켜야 할 기술 규칙
- `reference/`: API/DB 등 코드와 1:1로 맞아야 하는 레퍼런스
- `decisions/`: 장기 영향을 주는 설계 결정
- `roadmap.md`: 아직 구현하지 않은 기능과 기술

## Source of truth

충돌 시 우선순위는 다음과 같다.

1. 현재 실행 코드와 자동 테스트
2. DB DDL 및 런타임 설정
3. `decisions/`에서 `Accepted` 상태인 최신 결정
4. `architecture/` 문서
5. `reference/` 문서
6. `roadmap.md`

미래 계획을 현재 구현처럼 작성하지 않는다.

## AI 작업 원칙

Claude Code와 Codex는 다음 규칙을 따른다.

- 현재 아키텍처와 미래 계획을 혼합하지 않는다.
- Android와 Backend의 책임 경계를 임의로 바꾸지 않는다.
- Provider별 코드를 공통 Domain으로 누출시키지 않는다.
- 외부 API 호출 중 DB Transaction을 유지하지 않는다.
- 새로운 라이브러리나 인프라는 실제 문제와 도입 이유가 있을 때만 추가한다.
- Entity/DDL/API를 변경하면 관련 문서를 같은 변경에서 갱신한다.
- 새로운 장기 설계 결정은 `decisions/`에 기록한다.
- 아직 구현하지 않은 기술을 구현된 것처럼 설명하지 않는다.
