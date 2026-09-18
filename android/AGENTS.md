# Android AGENTS.md

These instructions apply to files under `android/`.

Root project rules in `../AGENTS.md` still apply.

## Read before coding or reviewing

For Android work, read the relevant documents when they exist:

- `../docs/architecture/storage-integration.md`
- `../docs/architecture/authentication-boundary.md`
- `../docs/architecture/local-cache.md`
- `../docs/development/android.md`

Inspect the existing Android implementation before introducing new routes, repositories, clients, cache layers, or wrappers.

## Architecture

- Provider authentication and authorization belong to Android.
- Provider API access must stay behind Provider-specific client boundaries such as `StorageClient` implementations.
- Keep Provider-specific SDK types, DTOs, tokens, and authentication objects out of common Storage UI and common ViewModel code.
- Reuse the common `StorageRoute`, `StorageScreen`, and `StorageViewModel` structure where applicable.
- Provider-specific routes should contain only Provider-specific authentication or behavior.
- Do not create a Provider-specific duplicate of common Storage UI when the shared structure can support the feature.
- Prefer existing Android abstractions before introducing new wrappers or interfaces.
- Do not move Provider authentication state, Provider API access, or Provider credentials into Backend to simplify Android implementation.

## Provider boundary

- Keep Provider SDK and response types behind Provider-specific clients and mapping boundaries.
- Convert Provider-specific responses into Ariadne models before exposing them to common Storage flows.
- Common UI and ViewModel code should operate on Ariadne models rather than Provider DTOs.
- Do not introduce direct Provider API calls into common UI or ViewModel layers.
- Do not bypass an existing `StorageClient` boundary without a demonstrated need.

## Storage and cache

- Room is a local metadata cache, not the Provider source of truth.
- Preserve offline metadata lookup behavior when changing Storage cache logic.
- Do not treat cached metadata as proof that the underlying file is available offline.
- Do not share Android Room entities directly with Backend JPA entities.
- Keep Provider DTO → Android model/cache mapping explicit.
- Preserve stable list ordering and avoid unnecessary full-list replacement when sync updates cached data.

## Backend API integration

- Backend communication must use Provider-independent Ariadne API contracts.
- Do not send Provider authentication objects, access tokens, refresh tokens, or Provider SDK models to Backend unless an explicit architecture decision requires it.
- Keep Backend request and response DTO mapping separate from Provider DTO mapping.
- When consuming a changed Backend API contract, verify request fields, response fields, validation expectations, HTTP behavior, and compatibility.

If the linked Issue explicitly scopes Backend API implementation separately from Android integration, do not implement the Android consumer unless the current task includes it.

## Code Review Rules

When reviewing Android changes, flag changes that:

- bypass existing `StorageClient` or Provider-specific client boundaries without a demonstrated need
- expose Provider-specific SDK types, DTOs, tokens, or authentication objects to common Storage UI or common ViewModel code
- introduce direct Provider API access into common UI or ViewModel layers
- duplicate common `StorageRoute`, `StorageScreen`, or `StorageViewModel` behavior for a Provider without necessity
- break Room-based offline metadata lookup behavior
- treat Room cache as the Provider source of truth or as proof that the underlying file is available offline
- couple Android Room entities directly to Backend JPA entities
- send Provider credentials, authentication objects, or Provider SDK models to Backend without an explicit architecture decision
- consume an in-scope Backend API contract inconsistently with its request, response, validation, or HTTP behavior

Respect the linked Issue Scope. If Android integration is explicitly deferred, do not expand a Backend-only task into Android implementation during review.

Focus findings on behavior, architecture, compatibility, regression, and data-flow risks rather than stylistic preferences.

Leave formatting and lint issues to configured automated checks.

Safe paths include:

- keep Provider-specific behavior behind existing client boundaries
- map Provider-specific responses into Ariadne models before common UI use
- preserve Room cache and offline lookup behavior
- reuse the common Storage UI flow where applicable

## Comments

For new classes, add concise comments describing responsibility and scope when that is not obvious.

For non-obvious functions or important fields, explain why they exist rather than restating the code.

Avoid comments that merely repeat the implementation.

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

If a device, emulator, Provider login, or another external dependency prevents verification, report that explicitly.
