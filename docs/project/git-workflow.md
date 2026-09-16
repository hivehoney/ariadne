# Git Workflow

## 장기 Branch

```text
master
└── develop
    ├── backend
    ├── android
    ├── agent
    └── infra
```

| Branch | 역할 |
|---|---|
| `master` | 릴리스된 안정 버전 |
| `develop` | 전체 프로젝트 통합 |
| `backend` | Backend 통합 |
| `android` | Android 통합 |
| `agent` | Desktop Agent 통합 |
| `infra` | Infrastructure 통합 |

장기 Branch에서 직접 기능 개발하지 않는다.

## 작업 Branch

```text
feat/<area>/<feature>
fix/<area>/<feature>
refactor/<area>/<feature>
```

예:

```text
feat/android/storage-cache
feat/backend/metadata-sync
fix/android/google-drive-disconnect
```

Android 작업 Branch는 `android`, Backend 작업 Branch는 `backend`를 기준으로 생성한다.

## Worktree

여러 작업을 독립적으로 진행하기 위해 Git Worktree를 사용한다.

```text
Worktree = 로컬 작업공간
Branch   = Git 변경 이력
```

Repository 밖에 Worktree를 둔다.

예:

```text
D:\dev\project\
D:\dev\project-worktrees\android-storage-cache\
D:\dev\project-worktrees\backend-metadata-sync\
```

## Merge 흐름

```text
feat/fix
↓
android | backend | agent | infra
↓
develop
↓
release/*
↓
master
```
## Commit Message

Conventional Commit 형식을 사용하며, 변경 영역을 구분하기 위해 `scope`를 반드시 포함한다.

```text
<type>(<scope>): <실제 변경 기능>
```

### Type

| Type       | 용도              |
| ---------- | --------------- |
| `feat`     | 새로운 기능 추가       |
| `fix`      | 버그 수정           |
| `refactor` | 기능 변경 없는 구조 개선  |
| `docs`     | 문서 변경           |
| `test`     | 테스트 추가·수정       |
| `chore`    | 빌드, 설정, 개발환경 변경 |

### Scope

현재 Repository의 주요 영역을 기준으로 작성한다.

```text
android
backend
infra
docs
agent
```

필요하면 기능 영역을 더 구체적인 scope로 사용할 수 있지만, 기본적으로는 어느 애플리케이션의 변경인지 바로 알 수 있도록 위 영역을 우선한다.

### 예시

```text
feat(android): google drive 인증 및 파일 조회 연동
feat(android): storage repository 및 cache 매핑 구조 추가
feat(android): room 기반 storage metadata cache 구현
fix(android): google drive 연결 해제 오류 수정
refactor(android): storage 화면을 provider 공통 구조로 분리

feat(backend): metadata sync 기능 구현
feat(backend): file 및 file location 저장 구조 추가
fix(backend): metadata sync transaction 처리 오류 수정
refactor(backend): storage provider resolver 구조 개선

chore(infra): mysql docker compose 설정 추가
docs(docs): storage architecture 문서 갱신
```

다음과 같이 영역이 없거나 변경 내용을 알 수 없는 메시지는 사용하지 않는다.

```text
feat: 기능 추가
feat: backend 기능 추가
fix: 오류 수정
chore: 설정 변경
```

Commit 제목만 보고도 **어느 영역에서 어떤 기능이 변경되었는지 알 수 있어야 한다.**

예를 들어 Git Log에서 다음과 같이 표시될 수 있다.

```text
feat(android): storage repository 및 cache 매핑 구조 추가
taeukHa
2026-08-25 오전 4:43
```

이때 실제 Commit Message는 다음 한 줄이다.

```text
feat(android): storage repository 및 cache 매핑 구조 추가
```

작성자와 Commit 시각은 Git Metadata이므로 Commit Message에 직접 작성하지 않는다.
