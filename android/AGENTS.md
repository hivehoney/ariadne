# Android AGENTS.md

These instructions apply to files under `android/`.

Root project rules in `../AGENTS.md` still apply.

## Read before coding

For Android work, read the relevant documents when they exist:

- `../docs/architecture/storage-integration.md`
- `../docs/architecture/authentication-boundary.md`
- `../docs/architecture/local-cache.md`
- `../docs/development/android.md`

Inspect the existing Android implementation before introducing new routes, repositories, clients, cache layers, or wrappers.

## Architecture

- Provider API access must stay behind Provider-specific client boundaries such as `StorageClient` implementations.
- Keep Provider-specific SDK types, DTOs, tokens, and authentication objects out of common Storage UI and common ViewModel code.
- Reuse the common `StorageRoute`, `StorageScreen`, and `StorageViewModel` structure where applicable.
- Provider-specific routes should contain only Provider-specific authentication or behavior.
- Do not create a Provider-specific duplicate of common Storage UI when the shared structure can support the feature.
- Prefer existing Android abstractions before introducing new wrappers or interfaces.

## Storage and cache

- Room is a local metadata cache, not the Provider source of truth.
- Preserve offline metadata lookup behavior when changing Storage cache logic.
- Do not treat cached metadata as proof that the underlying file is available offline.
- Do not share Android Room entities directly with Backend JPA entities.
- Keep Provider DTO → Android model/cache mapping explicit.
- Preserve stable list ordering and avoid unnecessary full-list replacement when sync updates cached data.

## Code Review Rules

When reviewing Android changes, flag changes that:

- bypass existing `StorageClient` or Provider-specific client boundaries without a demonstrated need
- expose Provider-specific SDK types, DTOs, tokens, or authentication objects to common Storage UI or common ViewModel code
- duplicate common `StorageRoute`, `StorageScreen`, or `StorageViewModel` behavior for a Provider without necessity
- break Room-based offline metadata lookup behavior
- treat Room cache as the Provider source of truth or as proof that the underlying file is available offline
- couple Android Room entities directly to Backend JPA entities

Focus findings on behavior, architecture, regression, and data-flow risks rather than stylistic preferences.

Safe path: keep Provider-specific behavior behind the existing client boundaries and reuse the common Storage UI flow where applicable.

## Comments

For new classes, add concise comments describing responsibility and scope when that is not obvious.

For non-obvious functions or important fields, explain why they exist rather than restating the code.

## Build and test

From `android/`:

```powershell
.\gradlew.bat test
.\gradlew.bat assembleDebug
.\gradlew.bat lintDebug
```

When instrumentation tests are relevant and an emulator or device is available:

```powershell
.\gradlew.bat connectedAndroidTest
```

Do not claim Android verification passed unless the relevant command was actually run.

If device, emulator, Provider login, or another external dependency prevents verification, report that explicitly.
