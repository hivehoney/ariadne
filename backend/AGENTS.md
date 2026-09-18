# Backend AGENTS.md

These instructions apply to files under `backend/`.

Root project rules in `../AGENTS.md` still apply.

## Read before coding or reviewing

For Backend work, read the relevant documents when they exist:

- `../docs/architecture/domain-model.md`
- `../docs/architecture/metadata-sync.md`
- `../docs/architecture/authentication-boundary.md`
- `../docs/development/kotlin-jpa.md`
- `../docs/reference/database.md`

Inspect the existing Domain, application, persistence, and adapter boundaries before introducing new abstractions.

## Domain rules

- Keep `File` and `FileLocation` responsibilities separate.
- `StorageSource` represents a connected Storage or Device.
- `(storageSourceId, externalId)` identifies a Provider file location.
- `(storageSourceId, externalId)` is not proof that files across different `StorageSource`s contain identical content.
- Do not merge cross-storage files solely because their names match.
- Do not introduce Provider-specific fields into common Domain models without a demonstrated cross-Provider need.

## Transaction and external I/O

- Keep external network I/O outside DB transactions.
- Keep transactional DB work inside focused persistence boundaries.
- Do not add `@Transactional` to an orchestration or application service if that would keep a transaction open while waiting on external I/O.
- External API success and DB commit are not one distributed ACID transaction.
- If a new flow requires cross-system consistency, evaluate the failure and compensation model explicitly instead of extending a DB transaction around the network call.

## API and validation

- Keep Backend API contracts Provider-independent unless a Provider-specific endpoint is explicitly required.
- Validate externally supplied request data before persistence.
- Keep API validation constraints consistent with known persistence constraints where applicable.
- Requests that violate known input constraints should fail at the API boundary rather than surfacing as avoidable persistence errors.
- Do not silently coerce malformed or missing input into valid domain values when that changes the meaning of the request.
- Keep HTTP error behavior explicit and testable.

When an API change is intentionally Backend-only because the linked Issue stages Android integration separately, do not expand the task into Android implementation.

The root `AGENTS.md` API contract rules still apply.

## Kotlin and JPA

- Do not use Kotlin `data class` as the default JPA Entity pattern.
- Consider Hibernate proxy requirements and Lazy Loading behavior.
- Do not solve N+1 problems by changing all relationships to `EAGER`.
- Prefer query-specific Fetch Join, `EntityGraph`, Projection, or dedicated queries when needed.
- Keep JPA relationship constraints and DB constraints consistent.
- When Entity mappings change, inspect the corresponding DDL and database documentation.
- Do not rely only on application-level assumptions when the database requires an explicit uniqueness or integrity constraint.

## Provider boundary

- Do not extend legacy Backend credential or OAuth code into new Provider flows unless the task explicitly authorizes that direction.
- Do not introduce new Provider authentication, authorization, or Provider API access responsibilities into Backend without an explicit architecture decision.
- Do not expose Provider SDK or Provider response models through common Backend API or Domain boundaries.

## Persistence changes

When persistence behavior changes:

- inspect the affected Entity mappings
- inspect repository behavior
- inspect database constraints
- run relevant integration tests
- update database documentation when the schema or persistence contract changes

Do not introduce a schema change when the existing schema can satisfy the requirement without compromising correctness.

If a schema change is required and the task has not already authorized it, explain why before implementing it.

## Code Review Rules

When reviewing Backend changes, flag changes that:

- perform external network I/O while a DB transaction is active
- broaden `@Transactional` boundaries around orchestration that includes external I/O
- violate the `File` / `FileLocation` separation
- treat `(storageSourceId, externalId)` as proof of identical content across different `StorageSource`s
- merge cross-storage files solely because filenames match
- introduce request validation that is weaker than known persistence constraints and can turn invalid client input into avoidable server errors
- silently coerce malformed or missing request values into valid domain values when semantics change
- solve JPA N+1 problems by broadly switching relationships to `EAGER`
- extend legacy Backend credential or OAuth code into new Provider flows without an explicit architecture decision
- introduce Provider-specific SDK or response types into common Backend API or Domain boundaries
- make a schema change without a demonstrated requirement
- change persistence behavior without relevant tests when the behavior depends on JPA, database constraints, or Spring wiring

Respect the linked Issue Scope. If Android integration is explicitly Out of Scope for a Backend-only API task, do not report the missing Android consumer as a Backend defect solely because it is deferred.

Focus findings on correctness, validation, transaction safety, persistence behavior, compatibility, and architectural regressions.

Leave formatting and lint issues to configured automated checks.

Safe paths include:

- keep external I/O outside DB transactions
- keep persistence transactions focused on database work
- validate external requests before persistence
- preserve Provider-independent Backend contracts

## Build and test

From `backend/`:

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat ktlintCheck
```

Run relevant integration tests when changed behavior depends on persistence or Spring wiring.

Do not treat mocked Provider tests as successful real Provider E2E verification.

Do not claim Backend verification passed unless the relevant commands were actually run.

If Docker, MySQL, Provider credentials, or another required dependency is unavailable, report that limitation explicitly.
