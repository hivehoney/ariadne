# Pull Request Review Policy

This policy defines how Codex evaluates, prioritizes, and reports Pull Request review candidates in Ariadne.

Project architecture and implementation rules come from the applicable `AGENTS.md` files.

The linked Issue defines the current task scope.

This file does not replace or duplicate those sources of truth.

## Review sources and precedence

For a PR review, use these sources together:

1. the user's explicit review request
2. the linked Issue's Goal, Scope, Acceptance Criteria, Architecture Constraints, and Out of Scope
3. the repository root `AGENTS.md`
4. the closest applicable module `AGENTS.md`
5. the actual PR diff and current implementation
6. this review policy for finding severity, reporting, triage, and publication behavior

If documentation and implementation conflict, report the conflict rather than silently choosing whichever produces a finding.

Future or roadmap documentation is not evidence that a capability is currently implemented or required by the current PR.

## Review priorities

Prioritize findings in this order:

1. correctness and functional regressions
2. security and credential exposure
3. architecture boundary violations
4. API contract and compatibility problems
5. transaction, persistence, and data-integrity risks
6. missing validation for changed behavior
7. missing tests for changed behavior
8. limited maintainability problems with concrete future failure risk

Do not prioritize stylistic preference over functional or architectural risk.

## Evidence standard

Report a review candidate only when there is a concrete issue introduced, exposed, or made materially relevant by the reviewed change.

A useful review candidate must have a plausible failure condition or a concrete project-rule violation.

Prefer evidence from:

- changed code
- directly affected surrounding code
- applicable project rules
- linked Issue requirements
- persistence or API constraints
- existing tests and behavior

Do not report speculative failure modes that require unsupported assumptions.

Do not report unrelated pre-existing problems merely because they are visible while reviewing the PR.

## Scope handling

The linked Issue is the source of truth for the current task boundary.

Before treating missing functionality as a defect, check:

- Goal
- Scope
- Acceptance Criteria
- Architecture Constraints
- Out of Scope

If work is explicitly deferred or Out of Scope, do not classify its absence as an in-scope defect unless that omission creates a concrete correctness, security, compatibility, or runtime problem in the current change.

For staged cross-module work, inspect both sides of an API contract but do not require both sides to be implemented in the same PR when the Issue intentionally separates them.

A materially relevant observation outside the current task may be shown to the human as:

```text
Scope: Out of Scope
```

It is not eligible for publication as an accepted current-PR defect unless the human explicitly reclassifies it after discussion.

## Severity

Use the following severity levels.

### P0 — Critical

Use only for an immediate catastrophic risk such as:

- severe security compromise
- destructive or unrecoverable data loss
- system-wide production failure

P0 should be rare.

### P1 — High

Use when the change can cause a major functional failure, serious security exposure, data corruption, or a major architecture/compatibility break in normal use.

### P2 — Medium

Use for concrete correctness, validation, compatibility, persistence, transaction, or regression problems that should be fixed but are not catastrophic.

### P3 — Low

Use for a minor but real problem with limited impact, including maintainability issues only when they create a concrete future failure or misuse risk.

Do not use P3 for personal style preferences.

## Finding requirements

Every review candidate must contain:

- stable finding ID for the reviewed commit
- severity
- concise title
- scope classification
- repository-relative file path when applicable
- affected line or line range when available
- concrete problem
- concrete impact
- minimal suggested direction

The finding should explain the defect, not write the implementation patch.

A developer reading the finding should be able to understand what behavior is wrong and why it matters without reconstructing the entire review conversation.

## Finding IDs

Use sequential IDs per review run:

```text
F-001
F-002
F-003
```

IDs are stable only for the specific reviewed PR head commit.

If the PR head changes and a re-review is performed, create a fresh finding set rather than pretending old IDs describe the new commit.

## Do not report

Do not create review findings for:

- formatting already enforced by tooling
- lint issues already deterministically enforced by CI
- personal naming or style preferences when the current convention is valid
- speculative refactoring opportunities
- optional abstractions
- unrelated cleanup
- roadmap capabilities not required by the current Issue
- capabilities explicitly listed as Out of Scope, unless their absence causes a concrete current-scope failure
- broad framework or infrastructure recommendations without a concrete requirement

Do not recommend Redis, Kafka, Elasticsearch, WebFlux, Spring Cloud Gateway, Kubernetes, ArgoCD, Vault, semantic search, LLM classification, or similar architectural additions merely as review improvements.

## CI relationship

A passing CI result does not prove semantic correctness.

CI should handle deterministic checks that are configured for the project, including formatting and lint.

Code review should focus on semantic issues that require project context, including:

- behavior and correctness
- architecture boundaries
- security
- API compatibility
- transaction boundaries
- persistence and data integrity
- regression risk
- missing behavioral tests

Do not duplicate CI output as review findings unless the CI failure reveals a separate semantic issue that needs explanation.

## Human Triage

Codex review candidates are not automatically accepted defects.

After review candidates are presented, a human decides their disposition.

Allowed states:

- `Accepted`
- `Out of Scope`
- `False Positive`
- `Needs Discussion`

Only findings explicitly marked or selected as `Accepted` may be published to GitHub by the review workflow.

Human Triage must happen before any GitHub review write.

## Publication policy

Before publication:

- verify that the PR head still matches the reviewed commit
- publish only human-accepted findings
- prefer exact inline locations from the PR diff
- never attach a finding to an unrelated line merely to make it inline
- use repository-relative paths
- use GitHub's `line`/`side` fields rather than deprecated `position` when possible
- use Pull Request Review event `COMMENT`

Do not automatically use:

- `APPROVE`
- `REQUEST_CHANGES`

An accepted finding that cannot be placed on a valid diff line may be included in the overall review body instead.

Unselected, Out of Scope, False Positive, and unresolved Needs Discussion items must not be published as accepted findings.

## Review-only behavior

During the review workflow, Codex must not:

- modify application code
- auto-fix findings
- commit
- push implementation changes
- merge
- resolve GitHub review comments
- broaden the linked Issue Scope

The review workflow ends after accepted findings are published and the result is reported.

Implementation changes belong to the implementation agent after the Human Triage gate.
