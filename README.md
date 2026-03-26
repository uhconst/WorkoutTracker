# WorkoutTracker

A Kotlin Multiplatform workout tracking app for **Android**, **iOS**, and **Wear OS**. Track exercises grouped by muscle group, log weights over time, and view your data on the wrist via a Wear OS companion app.

---

## Table of Contents

- [Features](#features)
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Versioning](#versioning)
- [AI / Claude Code Guidelines](#ai--claude-code-guidelines)

---

## Features

- Email/password login, Google OAuth, OTP-based password recovery
- Browse exercises grouped by muscle group with weight history
- Add, edit, and delete exercises and muscle groups
- Filter exercises by one or more muscle groups
- Wear OS companion app (read-only, session synced from phone via Wearable Data Layer)
- Haptic feedback with platform-specific implementations (Android + iOS)

---

## Project Structure

```
WorkoutTracker/
├── composeApp/                  # Shared phone app (Android + iOS)
│   └── src/
│       ├── commonMain/          # Shared UI, ViewModels, Domain, Repositories
│       ├── androidMain/         # Android entry point, WearSessionSync, Haptic impl
│       └── iosMain/             # iOS entry point, Haptic impl
├── wearApp/                     # Wear OS companion (Android only)
│   └── src/main/kotlin/
│       ├── data/                # DTOs, WearSessionRepository, WearExerciseRepository
│       ├── domain/              # Domain models (watch-side)
│       ├── presentation/        # Wear screens and ViewModels
│       └── di/                  # Koin DI module
├── iosApp/                      # iOS native wrapper
├── gradle/libs.versions.toml    # Centralized dependency versions
└── build.gradle.kts
```

---

## Tech Stack

| Category | Library | Version |
|---|---|---|
| Language | Kotlin Multiplatform | 2.2.21 |
| UI | Compose Multiplatform | 1.9.3 |
| Wear UI | Wear Compose (Material) | 1.4.1 |
| Backend | Supabase (Auth, PostgREST, Realtime) | 3.2.6 |
| HTTP | Ktor (OkHttp / Darwin) | 3.3.2 |
| DI | Koin | 4.1.1 |
| Navigation | Jetpack Navigation Compose (type-safe) | 2.9.1 |
| Serialization | Kotlinx Serialization JSON | — |
| Testing | JUnit 4, MockK, Turbine | — |
| Wearable Sync | Play Services Wearable | 18.2.0 |

- **Android:** compileSdk 36, minSdk 24 (phone) / 26 (wear)
- **iOS:** via KMP + Compose Multiplatform

---

## Architecture

The app follows **Clean Architecture** with **MVVM** in the presentation layer, organized **by feature**.

```
Presentation  →  Domain (interfaces)  →  Data (implementations)  →  Supabase
(ViewModel)       (Repository)            (RepositoryImpl)
```

### Layer responsibilities

| Layer | Contains | Rules |
|---|---|---|
| `presentation/` | Composable screens, ViewModels, UI state | Depends only on domain interfaces. Never imports Supabase directly. |
| `domain/` | Repository interfaces, domain models | No Android/platform imports. Pure Kotlin. |
| `data/` | Repository implementations, DTOs, mappers | Knows about Supabase. Maps DTOs → domain models before returning. |

### Feature modules (under `commonMain`)

- `authentication/` — Login, signup, OTP, password reset
- `workout/` — Exercise list, add/edit exercise
- `muscle/` — Muscle group management
- `core/` — Shared UI components, theme, haptic feedback abstraction, navigation

### State management

- ViewModels expose `StateFlow` / `SharedFlow`; screens observe with `collectAsState()`
- Side effects (navigation, snackbars) use `SharedFlow` or `LaunchedEffect`
- Derived state uses `combine()` — never computed inside composables

### Wear OS integration

The watch app is read-only. It has no login screen. The phone pushes the Supabase session (access + refresh tokens) to the watch via the **Wearable Data Layer API** at path `/supabase_session`. The watch reads this on startup and imports it into its local Supabase client with `autoRefresh = true`.

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog or newer
- Xcode 15+ (for iOS target)
- A Supabase project with `muscle_groups`, `exercises`, and `weight_logs` tables

### Setup

1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Run `composeApp` on an Android emulator or device
5. For Wear OS: pair a Wear OS emulator and run `wearApp`

> **Note:** Supabase URL and anon key are currently hardcoded in `composeApp/src/commonMain/.../di/Modules.kt` and `wearApp/.../di/WearModules.kt`. For production, move these to a `local.properties` file or environment variables and exclude from version control.

---

## Testing

```bash
./gradlew test                    # Unit + common tests
./gradlew connectedAndroidTest    # Instrumented UI tests (requires device/emulator)
```

### Test structure

| Type | Location | Tools |
|---|---|---|
| ViewModel unit tests | `androidUnitTest/` | JUnit 4, MockK, Turbine |
| Common logic tests | `commonTest/` | JUnit 4 |
| Compose UI tests | `androidInstrumentedTest/` | Compose UI Test, JUnit 4 |

### Conventions

- **Every ViewModel must have a corresponding unit test** in `androidUnitTest/`
- Mock repositories with MockK; never mock Supabase directly
- Use `Turbine` for testing `Flow` / `StateFlow` emissions
- UI tests assert on visible text and semantics, not on internal state
- Use `StandardTestDispatcher` + `advanceUntilIdle()` for coroutine tests

---

## Versioning

Both `composeApp` (phone/iOS) and `wearApp` (Wear OS) share the same `applicationId` (`com.uhc.workouttracker`). This means the Google Play Store treats them as the same application family, and **their `versionCode` values must never be equal** — Play requires each uploaded APK/AAB to have a strictly unique `versionCode` within an app.

### Rules

- `versionCode` must be a positive integer that increases with every release.
- `versionCode` values across `composeApp` and `wearApp` must not collide.
- `versionName` is a human-readable string (e.g. `"0.2.0"`) and follows [Semantic Versioning](https://semver.org/): `MAJOR.MINOR.PATCH`.
  - Bump `PATCH` for bug fixes.
  - Bump `MINOR` for new features (backwards-compatible).
  - Bump `MAJOR` for breaking changes.
- `composeApp` and `wearApp` are versioned independently — their `versionName` strings do not need to match.

### Current versions

| Module | `versionCode` | `versionName` |
|---|---|---|
| `composeApp` (phone + iOS) | 4 | 0.2.0 |
| `wearApp` (Wear OS) | 3 | 0.1.0 |

---

## AI / Claude Code Guidelines

This section documents conventions that should be followed when using AI assistants (Claude Code or similar) to work on this codebase.

### Architecture rules

- Always place new features under the correct layer: `presentation/`, `domain/`, `data/`
- Repository interfaces live in `domain/`; implementations live in `data/`
- ViewModels must not import Supabase, Ktor, or any external SDK directly — only repository interfaces
- DTOs belong in `data/`; domain models in `domain/`; never expose DTOs to ViewModels or screens

### Code style

- Use `runCatching { }` for all Supabase calls; **never let exceptions propagate unhandled** from a coroutine launched in `viewModelScope`
- Error messages shown to users must be **static, user-friendly strings** — never `exception.message`, never raw HTTP responses, never URLs or tokens
- Prefer `StateFlow` for UI state; use `SharedFlow` for one-shot events (navigation, snackbars)
- New screens must be stateless composables — all state comes from parameters, all events are callbacks

### Adding a new feature (checklist)

1. Create domain model in `feature/domain/model/`
2. Create repository interface in `feature/domain/repository/`
3. Create DTO + mapper in `feature/data/`
4. Implement repository in `feature/data/repository/`
5. Register the repository binding in `di/Modules.kt`
6. Create ViewModel with `runCatching` error handling
7. Register ViewModel with `viewModelOf(::MyViewModel)` in `di/Modules.kt`
8. Create screen composable; add route to `NavRoute.kt` and `NavHost`
9. Write ViewModel unit test

### What NOT to do

- Do not inject `SupabaseClient` into ViewModels — use repositories
- Do not use `mutableStateOf` in ViewModels — use `MutableStateFlow`
- Do not hardcode user-facing strings in `onFailure` blocks with exception details
- Do not skip `runCatching` on any suspend function that calls a remote API
- Do not add `Room` or local database without a discussion — the app intentionally uses Supabase as the single source of truth

### Wear OS rules

- The watch app is **read-only** — it only reads data, never writes back to Supabase
- Session is always sourced from the phone via `WearSessionRepositoryImpl` — never prompt the user to log in on the watch
- Always decode the real `exp` claim from the JWT when calling `importSession()` — never hardcode `expiresIn`
- If `expiresIn == 0L`, call `supabase.auth.refreshCurrentSession()` before any API call

### Sensitive data

- Never log full tokens — log only prefixes (`token.take(10) + "…"`)
- Never display raw exception messages, Supabase URLs, or HTTP headers to the user
- The `apikey` header (anon key) is acceptable in the app bundle but must not appear in logs or UI

### Compile check

**Always verify the build compiles before finishing any code change:**

```bash
./gradlew :composeApp:compileDebugKotlin   # fast — Kotlin only, no APK
./gradlew :wearApp:compileDebugKotlin      # if wearApp was touched
```

Common pitfalls:
- `animateColorAsState` is in `androidx.compose.animation` — **not** `androidx.compose.animation.core`
- `animateFloatAsState` is in `androidx.compose.animation.core`
- `border` (Modifier) is in `androidx.compose.foundation`
- Prefer checking an existing file that already imports the same symbol rather than guessing the package

### No CI/CD yet

There is no automated pipeline. Before merging, manually run:

```bash
./gradlew test
./gradlew connectedAndroidTest   # on a device/emulator
```
