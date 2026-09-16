@AGENTS.md

# CLAUDE.md

This file defines Claude Code-specific working instructions for this repository.

Project-wide architecture, security, Git, API, and documentation rules are defined in the root `AGENTS.md`.

Module-specific implementation, build, and test rules are defined in the applicable module `AGENTS.md` files.

Do not duplicate or override those rules here.

## Role

In the standard Ariadne workflow, Claude Code acts as the primary implementation agent.

Claude Code must run the relevant verification required for the affected modules and self-review its own changes before reporting completion.

Independent Pull Request review is performed separately after implementation, and the final merge decision remains with the human developer.

## Development workflow

Before editing:

1. Read the user's task or GitHub Issue carefully.
2. Read the relevant project documentation required by `AGENTS.md`.
3. Read the applicable module `AGENTS.md` files for every affected module.
4. Inspect the existing implementation before proposing changes.
5. Identify the affected modules and files.
6. Check whether the requested work fits the current architecture.
7. Prefer the smallest change that satisfies the requirement.

If the task appears to require an architecture change, follow the architecture-change process defined in `AGENTS.md`.

Do not begin by introducing a new framework, dependency, abstraction, or infrastructure component unless the existing implementation cannot reasonably satisfy the requirement.

## Before implementation

For non-trivial tasks, briefly determine:

- the current behavior
- the affected modules
- the relevant existing abstractions
- the intended change
- the verification plan

Do not produce a large implementation plan for a trivial change.

If the current implementation or documentation conflicts with the requested direction, report the conflict before making an architectural change.

## While editing

- Reuse existing abstractions before creating new ones.
- Avoid unrelated refactoring.
- Avoid broad renaming or file movement unless required by the task.
- Keep changes focused on the requested behavior.
- Do not silently change an existing architecture decision.
- Do not remove legacy or apparently unused architecture code unless the task explicitly includes that cleanup.
- Keep comments concise and focused on responsibility, intent, or non-obvious reasoning.

When adding a new class, include a concise class-level comment explaining its responsibility and scope when that is not already obvious from the code.

When adding non-obvious functions or important fields, explain why they exist rather than restating what the code does.

## Self-review

Before reporting completion:

1. Review the full diff.
2. Confirm that only intended files were modified.
3. Check for debug logs, temporary code, TODOs, commented-out code, and accidental formatting-only changes.
4. Check for architecture boundary violations.
5. Check whether secrets, tokens, credentials, or sensitive values were introduced.
6. Confirm documentation changes are included when required by `AGENTS.md`.
7. Run the checks required by the applicable `AGENTS.md` files for every affected module.
8. Re-check the original task and acceptance criteria for missed requirements.

Do not claim that Build, Test, Lint, E2E, or external Provider verification passed unless the corresponding command or verification was actually performed.

If verification could not be completed because a required emulator, device, database, external Provider, credential, or environment was unavailable, report that explicitly.

## Git behavior

Claude Code may inspect Git state as needed.

When working in a local checkout and before editing code, check:

```text
git status
git branch --show-current
git worktree list
```

Follow the Git rules defined in `AGENTS.md`.

Do not switch branches or create/remove Worktrees unless the task explicitly requires it.

When a meaningful development unit is complete, suggest one appropriate commit message using the format defined in `AGENTS.md`.

Do not commit, push, merge, rebase, reset, or force-push unless explicitly requested.

## Completion report

When implementation is complete, report only information useful for review.

Include:

1. What changed
2. Main files changed
3. Build / Test / Lint commands actually run
4. Verification result
5. Verification that could not be performed
6. Documentation updated
7. Suggested commit message

Keep the report concise.

## Rules and Skills

Do not create `.claude/rules/` files merely to reorganize existing `AGENTS.md` instructions.

Add path-specific Claude-only rules only when they are genuinely Claude-specific and loading them globally is wasteful or confusing.

Do not create a Skill for a one-off task.

Create a project Skill only after a multi-step workflow is repeated often enough to justify a reusable procedure.
