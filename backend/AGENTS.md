# Backend AGENTS.md

These instructions apply to files under `backend/`.

Root project rules in `../AGENTS.md` still apply.

## Read before coding

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
- Do not add `@Transactional` to an orchestration/application service if that would keep a transaction open while waiting on external I/O.
- External API success and DB commit are not one distributed ACID transaction.
- If a new flow requires cross-system consistency, evaluate the failure and compensation model explicitly instead of extending a DB transaction around the network call.

## Kotlin and JPA

- Do not use Kotlin `data class` as the default JPA Entity pattern.
- Consider Hibernate proxy requirements and Lazy Loading behavior.
- Do not solve N+1 problems by changing all relationships to `EAGER`.
- Prefer query-specific Fetch Join, `EntityGraph`, Projection, or dedicated queries when needed.
- Keep JPA relationship constraints and DB constraints consistent.
- When Entity mappings change, inspect the corresponding DDL and database documentation.

## Provider boundary
- Do not extend legacy Backend credential/OAuth code into new Provider flows unless the task explicitly authorizes that direction.

## Code Review Rules
When reviewing Backend changes, flag changes that:

- perform external network I/O while a DB transaction is active
- broaden `@Transactional` boundaries around orchestration that includes external I/O
- violate the `File` / `FileLocation` separation
- treat `(storageSourceId, externalId)` as proof of identical content across different `StorageSource`s
- merge cross-storage files solely because filenames match
- solve JPA N+1 problems by changing relationships broadly to `EAGER`
- extend legacy Backend credential or OAuth code into new Provider flows without an explicit architecture decision

Focus findings on correctness, transaction safety, persistence behavior, compatibility, and architectural regressions.
Safe path: keep external I/O outside DB transactions and keep persistence transactions focused on database work.

## Build and test

From `backend/`:

```powershell
.\gradlew.bat test
.\gradlew.bat build
.\gradlew.bat ktlintCheck
```

Run relevant integration tests when changed behavior depends on persistence or Spring wiring.
Do not treat mocked Provider tests as successful real Provider E2E verification.
If MySQL, Provider credentials, or another required dependency is unavailable, report that limitation explicitly.
