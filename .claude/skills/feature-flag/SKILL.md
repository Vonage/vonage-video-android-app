---
name: feature-flag
description: Add, rename or remove a configuration key end-to-end in the Vonage Video Android reference app (VERA) — app-config.json, the Gradle config generator (ConfigPropertiesGenerator / GenerateConfigTask), gradle/generated-config.properties, app/build.gradle.kts missingDimensionStrategy + BuildConfig, Config.kt, MeetingRoomFeature / MeetingRoomConfiguration, GetConfigTest, the shared JSON schema and docs. Use whenever the user wants to feature-gate something new, add a config toggle or setting, make a feature optional, asks why a new key generates nothing or fails the build with "Unrecognised top-level key", or mentions allowX keys, FEATURE_*_ENABLED, flavor dimensions, or the json-config plugin. For flipping an EXISTING key use the configure guide instead.
inclusion: fileMatch
fileMatchPattern: 'build-tools/gradle-config-plugin/**'
---

# VERA Android — Feature Flags & Config Keys, End-to-End

In kiro this attaches when editing the config generator; pull it in with `#vera-feature-flags` when working in `app/build.gradle.kts`, `Config.kt` or `MeetingRoomScreenRoute.kt`. Flipping an existing key is `/configure` (`#vera-configure`).

A key is "wired" only when every hop below exists. The generator is permissive inside a group (any new key under `meetingRoomSettings` becomes a constant automatically) but nothing *reads* that constant until you add the Kotlin side, so a half-done key compiles and silently does nothing. Walk the whole chain.

## First decide which kind of key it is

- **A. Optional capability with its own module** (chat, captions, archiving…): needs a `vonage-feature-*` module with `enabled`/`disabled` flavors, an app-side `missingDimensionStrategy`, and a `MeetingRoomFeature` runtime case. If the module doesn't exist yet, scaffold it first (`/new-feature-module`, `#vera-new-module` in kiro) and come back here.
- **B. Runtime-only toggle** (show a button, start muted, default layout): no flavor, no `BuildConfig`; `Config` field → `MeetingRoomConfiguration` / waiting-room / ViewModel.
- **C. Non-boolean value** (string/number): same as B, but the shared schema only allows extra **booleans** inside the settings groups, so CI schema validation fails until the React repo's `specs/app-config.schema.json` gains the key. Coordinate cross-platform before merging.

## Checklist (all kinds)

1. **`app-config.json`** — add the key inside the group it belongs to (`videoSettings`, `audioSettings`, `authSettings`, `waitingRoomSettings`, `meetingRoomSettings`). Keys are camelCase; booleans named `allowX` / `showX` by convention. Keep the file valid for iOS/React: a key Android doesn't implement is acceptable (see `defaultResolution`) but a key missing from the shared schema is not — see C above.
   - A **new top-level group** fails the build on purpose: `Unrecognised top-level key(s) in the app config: …`. Extend `SETTINGS_GROUPS` (object), `SCALAR_KEYS` (single value) or `IGNORED_KEYS` (doc-only block) in `build-tools/gradle-config-plugin/src/main/kotlin/com/vonage/gradle/ConfigPropertiesGenerator.kt`. `GenerateConfigTask` iterates the same lists, so one edit covers both outputs.
2. **Regenerate** — `./gradlew generateVonageConfig`. Confirm the two outputs:
   - `gradle/generated-config.properties` has `vonage.<prefix>.<snake_case_key>=…` (prefixes: `vonage.video`, `vonage.audio`, `vonage.auth`, `vonage.waitingRoom`, `vonage.meetingRoom`).
   - `app/build/generated/source/jsonConfig/com/vonage/android/config/AppConfig.kt` has `AppConfig.<Group>.<UPPER_SNAKE>` (`Boolean`/`String`/`Int`/`Double`; the generator ignores nested objects and arrays).
3. **`Config.kt`** (`app/src/main/java/com/vonage/android/config/`) — add the field to the `Config` data class and map it in `fromAppConfig()`. This is deliberately the only place `AppConfig` is read; don't read `AppConfig.*` elsewhere. Add the assertion to `app/src/test/java/com/vonage/android/config/GetConfigTest.kt`, which pins every mapping.
4. **Consume it**:
   - ViewModel → inject `GetConfig` and call `getConfig().yourKey`.
   - Composable without DI → `remember { Config.fromAppConfig() }`.
   - Meeting room → `MeetingRoomScreenRoute.kt`: UI toggles go into the `MeetingRoomConfiguration(...)` passed to `MeetingRoomBuilder.configuration(...)` (add the field to `vonage-meeting-room/.../api/MeetingRoomConfiguration.kt` first); capabilities go into `configuredMeetingRoomFeatures()` → `MeetingRoomFeature.<NAME>`.
5. **Kind A only — `app/build.gradle.kts`** `defaultConfig`: copy the chat block.
   ```kotlin
   val pollsProperty = configProps.getProperty("vonage.meetingRoom.allow_polls", "true")
   buildConfigField("boolean", "FEATURE_POLLS_ENABLED", "$pollsProperty")   // informational only
   missingDimensionStrategy("polls", pollsProperty.toEnabledString())
   // if vonage-meeting-room declares its own pollsEnabled/pollsDisabled flavors, add the 2nd arg:
   // missingDimensionStrategy("polls", p.toEnabledString(), p.toModuleFlavorString("polls"))
   ```
   Choose the default (`"true"`/`"false"`) to match the JSON default — it is what a build without a generated properties file uses. Then in `vonage-meeting-room`:
   - `api/MeetingRoomFeature.kt`: add the enum case (it's part of the public API; `MeetingRoomFeature.all` picks it up automatically).
   - gate the UI/plugin on `MeetingRoomFeature.POLLS in enabledFeatures` (see how `MeetingRoomScreen.kt` and `BottomBar.kt` do it) — never on `BuildConfig`.
   - `build.gradle.kts`: either pin `missingDimensionStrategy("polls", "enabled")` in `defaultConfig` (runtime filter only, keeps the variant count at 16) or add real `pollsEnabled`/`pollsDisabled` flavors only if the module itself has different source sets for it.
   - `vonage-meeting-room-sample-app/build.gradle.kts`: `missingDimensionStrategy("polls", "enabled")`.
6. **Docs & schema** — add the row to the field table in `docs/CONFIGURATION.md` (and `docs/CONFIG-SYSTEM.md` if flavor-backed); update the shared schema in the React repo when it would reject the key (non-boolean, or a new group). The Android Studio plugin and the TUI are schema-driven and need no change.
7. **Verify both states.** Flip the key to `false`, `./gradlew generateVonageConfig :app:assembleDebug`, confirm the behaviour (and for kind A that the `disabled` source set compiled — e.g. `./gradlew :app:dependencies --configuration debugCompileClasspath | grep feature-polls` shows the disabled variant, or simply that the enabled-only class is absent from the build). Flip back, regenerate, build again. Run `:app:testDebugUnitTest --tests '*GetConfigTest'`.

## Removing or renaming a key

Reverse the chain: JSON → `Config` + test → consumers → `app/build.gradle.kts` block → docs. Grep for the `AppConfig.<Group>.<CONST>` and the `vonage.<prefix>.<snake>` property name; both must have zero hits afterwards. Do **not** delete the parity-only keys (`videoSettings.defaultResolution`, `waitingRoomSettings.bypassWaitingRoom`) — they keep the file valid against the shared schema and interchangeable with the other platforms.

## Why it "isn't taking effect" — ranked by frequency

1. `generateVonageConfig` didn't run after the edit (or Android Studio wasn't re-synced for a flavor change).
2. The key was added to JSON but never to `Config.fromAppConfig()` — the constant exists, nobody reads it.
3. Branching on `BuildConfig.FEATURE_*` — informational; the flavor already swapped the implementation.
4. Expecting a compile-time effect from a runtime-only key (or expecting runtime re-enabling of a flavor-disabled module).
5. Inside `vonage-meeting-room`, chat/reactions/videofx/audiofx/settings are always the `enabled` variant; only the runtime `MeetingRoomFeature` set turns them off there.
6. The `missingDimensionStrategy` default in `app/build.gradle.kts` disagrees with the JSON default, so a clean checkout without the generated file builds the other variant.
