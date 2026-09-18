# AGENTS.md

## Project

Ariadne is the project name and repository name.

Ariadne is a monorepo containing:

- `android/`: Kotlin/Compose Android client
- `backend/`: Kotlin/Spring Boot backend
- `infra/`: local and deployment infrastructure
- `docs/`: product, architecture, decisions, and development documentation

Android and Backend are independent Gradle projects.

Do not assume there is a shared root Gradle build.

## Read before coding or reviewing

Before making architectural or cross-module changes, read:

- `docs/README.md`
- `docs/architecture/overview.md`
- `docs/decisions/README.md`

For area-specific work, read the `AGENTS.md` in every affected module before editing or reviewing files in that module.

- Changes under `android/` require `android/AGENTS.md`.
- Changes under `backend/` require `backend/AGENTS.md`.
- Cross-module changes require the applicable `AGENTS.md` files from all affected modules.

Documentation describing future work must not be treated as currently implemented behavior.

When documentation and the current implementation appear to conflict, do not silently choose one or rewrite architecture based on assumptions.

Report the conflict before changing architecture.

Preserve the current implementation boundary unless the task explicitly requires an architectural change.

## Architecture rules

- Provider authentication is handled by Android.
- Provider authorization is handled by Android.
- Provider API access is handled by Android.
- Backend owns the Ariadne metadata domain.
- Search, analysis, and organization features belong to Backend when implemented.
- Do not assume search, analysis, or organization features are already implemented unless confirmed by the current code.
- Provider access or refresh tokens must not be newly stored in Backend without an explicit architecture decision.
- Keep Provider-specific SDKs, authentication objects, and DTOs behind adapter or client boundaries.
- Do not expose Provider-specific models to common Storage UI or Backend domain code.
- Do not duplicate Provider API responsibilities between Android and Backend.
- Do not hold DB transactions while performing external network I/O.
- Do not introduce new infrastructure, frameworks, or major architectural capabilities without a concrete requirement and documented decision.

Examples include:

- Redis
- Kafka
- Elasticsearch
- WebFlux
- Spring Cloud Gateway
- Kubernetes
- ArgoCD
- Vault
- semantic search
- LLM-based classification

Roadmap items are not implementation requirements unless the current task explicitly asks for them.

## Development rules

Before changing code:

1. Read the relevant project and module instructions.
2. Read the relevant documentation.
3. Inspect the existing implementation.
4. Identify the affected modules.
5. Reuse existing abstractions where appropriate.
6. Prefer the smallest change that satisfies the requirement.

Do not:

- modify unrelated files
- perform unrelated large refactoring
- delete existing architecture code only because it appears unused
- change architecture boundaries silently
- add dependencies only for experimentation
- disable or weaken tests just to make a build pass
- treat mocked Provider tests as successful real Provider E2E tests
- expand the current task beyond the linked Issue's Scope without explicit approval

After changing code:

1. Run the checks defined by every applicable module `AGENTS.md`.
2. Review the diff.
3. Check whether documentation also needs updating.
4. Check whether the change introduces a new architecture decision.

Do not claim that a task is verified if the relevant checks were not actually run.

If verification cannot be completed because a required emulator, device, database, external Provider, credential, or environment is unavailable, report that limitation explicitly.

## Issue scope

The linked Issue defines the current task boundary.

Respect:

- Goal
- Scope
- Acceptance Criteria
- Architecture Constraints
- Out of Scope

Do not treat an explicitly staged or Out of Scope capability as a defect in the current change.

If a potential problem belongs outside the current Issue Scope:

- identify it separately when it is materially relevant
- do not silently expand the implementation
- do not require it for completion unless it blocks the stated Acceptance Criteria or creates a concrete compatibility, security, correctness, or runtime problem

## API contract

When an API contract changes, inspect all affected sides of the contract.

At minimum, consider:

- Backend endpoint
- Request DTO
- Response DTO
- Validation
- HTTP status
- Android API client
- Android mapping or consumer code
- backward compatibility where relevant

Checking both sides does not mean both sides must always be modified in the same task.

If the linked Issue explicitly stages Backend and Android work separately, preserve that Scope.

Do not report the intentionally deferred side as a current-task defect unless the partial change creates an actual compatibility, security, correctness, or runtime problem within the stated Scope.

For a task intended to complete an end-to-end contract change, do not consider the change complete until all in-scope consumers and producers are updated.

## Code Review Rules

When reviewing changes, prioritize:

1. correctness and functional regressions
2. security and credential exposure
3. architecture boundary violations
4. API contract and compatibility problems
5. transaction, persistence, and data-integrity risks
6. missing validation for changed behavior
7. missing tests for changed behavior

Apply the closest applicable module `AGENTS.md` in addition to these repository-wide rules.

Respect the linked Issue's Goal, Scope, Acceptance Criteria, Architecture Constraints, and Out of Scope.

Flag changes that:

- move Provider authentication, authorization, or Provider API access into Backend without an explicit architecture decision
- newly persist Provider access or refresh tokens in Backend without an approved architecture change
- expose Provider-specific SDK types, authentication objects, or DTOs across common module boundaries
- leave an in-scope API producer or consumer incompatible after a contract change
- introduce secrets, tokens, credentials, or sensitive values into code, logs, tests, configuration, or documentation
- treat roadmap functionality as already implemented
- weaken or disable tests merely to make verification pass

Do not flag an explicitly deferred Android or Backend implementation solely because it is absent when the linked Issue intentionally stages the work separately.

Leave formatting, lint, and other deterministic checks to automated tooling and CI when those checks are configured.

Avoid findings based only on personal style preference, optional refactoring, or unrelated cleanup when the existing project convention is valid.

For the repository PR review workflow, use `.agents/skills/pr-review/` when applicable. Its review policy defines finding format, severity, Human Triage, and publication behavior; it does not override project architecture rules in this file or a module `AGENTS.md`.

## Documentation

Update documentation in the same change when modifying behavior that affects it.

Typical mappings:

- architecture changes → `docs/architecture/`
- Entity or DDL changes → `docs/architecture/domain-model.md` and `docs/reference/database.md`
- API contract changes → API reference documentation
- Provider responsibility changes → `docs/architecture/authentication-boundary.md` or storage integration documentation
- long-term technical decisions → `docs/decisions/`
- future implementation plans → `docs/roadmap.md`

Do not duplicate detailed architecture documentation in `AGENTS.md`.

Keep detailed design and architecture sources of truth in `docs/`.

Do not describe planned functionality as already implemented.

## Security

Never commit:

- access tokens
- refresh tokens
- OAuth client secrets
- production passwords
- encryption keys
- private credentials

Do not print secrets or tokens in:

- logs
- tests
- documentation
- examples
- command output

Use placeholders for credential examples.

Do not weaken authentication, authorization, encryption, or credential handling merely to simplify development or testing.

## Git

When working in a local checkout and before editing code, check:

```powershell
git status
git branch --show-current
git worktree list
```

Do not develop directly on long-lived branches.

Current long-lived branches:

- `master`
- `develop`
- `android`
- `backend`

Feature and fix work should use dedicated branches according to:

- `docs/project/git-workflow.md`

Do not perform the following unless explicitly requested:

- `git commit`
- `git push`
- `git merge`
- `git rebase`
- `git reset --hard`
- force push

Commit messages use Conventional Commit format with a scope:

```text
<type>(<scope>): <actual change>
```

Common types:

- `feat`
- `fix`
- `refactor`
- `docs`
- `test`
- `chore`

Common scopes:

- `android`
- `backend`
- `infra`
- `docs`

Examples:

```text
feat(android): storage repository 및 cache 매핑 구조 추가
feat(android): google drive 인증 및 파일 조회 연동
feat(backend): metadata sync 기능 구현
fix(android): google drive 연결 해제 오류 수정
refactor(backend): storage provider resolver 구조 개선
chore(infra): mysql docker compose 설정 추가
```

Commit messages must describe the actual change.

Avoid vague messages such as:

```text
feat: 기능 추가
fix: 오류 수정
feat(backend): 기능 추가
```

## General principles

Prefer:

```text
problem
→ existing implementation
→ identified limitation
→ alternatives
→ decision
→ implementation
→ verification
```

Do not work in the opposite direction:

```text
technology to try
→ implementation
→ invent a reason to justify it
```

Prefer evidence from the current implementation and project documentation over assumptions.

Preserve existing architecture unless there is a concrete reason to change it.

If a task appears to require an architecture change:

1. identify why the current architecture cannot satisfy the requirement
2. explain the limitation
3. present reasonable alternatives
4. identify affected modules and documentation
5. if the task does not explicitly authorize the architecture change, report it before implementation
6. if the task explicitly authorizes the change, update the relevant architecture documentation together with the implementation
7. create or update an ADR if the change affects a long-term architectural boundary, responsibility, or technical decision
