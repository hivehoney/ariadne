---
name: pr-review
description: Review an Ariadne GitHub pull request with Codex, apply repository and module AGENTS.md rules, stop for Human Triage, and publish only explicitly accepted findings as GitHub inline review comments. Use when the user asks to review or re-review a PR, or to publish selected review findings.
---

# Ariadne PR Review

Use this workflow for Pull Request code review.

The reviewer is Codex.

Do not modify application code during this workflow.

The workflow has a mandatory Human Triage gate before any finding is published to GitHub.

## Inputs

Prefer an explicit Pull Request number or URL.

If no PR is supplied, infer one only when the current branch maps to exactly one open Pull Request. Otherwise ask for the PR number.

## 1. Load Pull Request context

Identify:

- repository root
- current branch
- Pull Request number and URL
- base branch
- head branch
- current PR head commit SHA
- PR title and description
- linked Issue or Issues when available
- changed files
- PR diff

Use GitHub CLI or an available GitHub integration to read this information.

Do not automatically checkout, switch, merge, or rebase branches.

For a local review, verify that the current checkout represents the Pull Request head being reviewed. If it does not, stop and report the mismatch instead of reviewing stale or unrelated local code.

Record the reviewed PR head commit SHA for the current review run.

## 2. Load project context

Always read the repository root `AGENTS.md`.

Determine affected modules from the changed files.

- If `backend/` is affected, read `backend/AGENTS.md`.
- If `android/` is affected, read `android/AGENTS.md`.
- If multiple modules are affected, read every applicable module `AGENTS.md`.

Read documentation referenced by the applicable `AGENTS.md` when it is relevant to the changed behavior.

Read the linked Issue before treating missing adjacent functionality as a defect.

Use the linked Issue's Goal, Scope, Acceptance Criteria, Architecture Constraints, and Out of Scope as the task boundary.

Read `references/review-policy.md` before producing findings.

If project documentation conflicts with the current implementation or the linked task, report the conflict. Do not silently invent a new architecture or treat future documentation as current implementation.

## 3. Review the Pull Request

Review the Pull Request diff against its base branch.

When the native Codex review workflow is available, use the configured review model for the review. Otherwise perform an equivalent read-only semantic review of the same PR diff.

Review surrounding code only as needed to understand the changed behavior.

Prioritize the rules from:

1. applicable `AGENTS.md` files
2. linked Issue scope and acceptance criteria
3. `references/review-policy.md`

Do not edit files.

Do not run an implementation or auto-fix workflow.

Do not commit or push.

## 4. Present review candidates

Assign stable IDs for this reviewed commit:

```text
F-001
F-002
F-003
```

For each candidate, present:

```text
F-001 [P2] <title>

Scope: In Scope | Out of Scope | Needs Human Judgment
File: <repository-relative path>
Line: <line or line range when available>

Problem:
<concrete issue>

Impact:
<why it matters>

Suggested direction:
<minimal direction, not an implementation patch>
```

Do not invent a line number when the issue cannot be tied to a specific changed line.

If an observation is explicitly outside the linked Issue Scope, mark it `Out of Scope` rather than presenting it as a current-PR defect.

Do not silently promote an Out of Scope observation into an accepted finding.

## 5. Human Triage gate

After presenting all review candidates, stop.

Do not publish anything to GitHub yet.

Do not modify code.

Wait for the human to explicitly classify or select findings.

Accepted examples:

```text
F-002, F-003 게시
```

```text
F-001 Out of Scope
F-002 Accepted
F-003 Accepted
```

Possible Human Triage states are:

- `Accepted`
- `Out of Scope`
- `False Positive`
- `Needs Discussion`

Only findings explicitly selected as `Accepted` are eligible for GitHub publication.

## 6. Verify before publication

Before any GitHub write:

1. re-read the Pull Request head commit SHA
2. compare it with the reviewed commit SHA
3. confirm GitHub authentication is available
4. confirm the selected finding IDs exist in the current review run

If the PR head changed after review, do not publish stale findings. Report that a re-review is required.

If GitHub authentication or Pull Request write permission is unavailable, stop and report the missing prerequisite.

## 7. Publish accepted findings

Use the GitHub Pull Request Review API through `gh api` or another configured GitHub integration.

Do not post accepted findings as ordinary Issue/Conversation comments when an inline Pull Request Review can represent them correctly.

Create one Pull Request Review for the accepted findings with:

- `commit_id`: the reviewed/current PR head SHA
- `event`: `COMMENT`
- one inline comment per accepted finding when the target line is commentable

For each inline comment use:

- repository-relative `path`
- `line`
- `side`
- optional `start_line` and `start_side` for a valid multi-line range
- `body`

Use `RIGHT` for additions or context lines on the new side of the diff and `LEFT` for deletions on the old side.

Do not use the deprecated diff `position` field when `line`/`side` can be used.

Prefix each published finding body with:

```text
[Local Codex Review][F-XXX][P0|P1|P2|P3]
```

If a selected finding is valid but cannot legally be attached to the intended line in the PR diff:

- do not guess another line
- include that finding in the overall review body instead
- clearly state that it was not published inline because the target line was not commentable

Never publish findings that the human did not accept.

Do not use `APPROVE` or `REQUEST_CHANGES` automatically.

## 8. Report publication result

After publication, report:

- PR number
- reviewed commit SHA
- accepted and published finding IDs
- findings published inline
- findings placed only in the review body
- findings not published
- GitHub review URL when available

Do not modify application code after publishing.

The implementation/fix phase belongs to Claude Code after Human Triage and GitHub publication.

## 9. Re-review

After Claude Code fixes accepted findings and the updated PR passes the required verification/CI, review the latest PR head again as a new review run.

Do not reuse prior finding IDs across a changed PR head as if they still referred to the same reviewed commit.

Generate a fresh set of finding IDs for the new review run and repeat the Human Triage gate.
