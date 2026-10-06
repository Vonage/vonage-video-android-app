---
name: new-feature-module
description: Create a new optional feature module (vonage-feature-<name>) for the Vonage Video Android reference app (VERA) with enabled/disabled product flavors, and integrate it into settings.gradle.kts, app/, vonage-meeting-room (manual DI container, MeetingRoomFeature), the sample app, config and docs. Use whenever the user wants to add a new feature module, scaffold a module, add a new meeting-room capability (polls, hand raise, whiteboard…), add a new signal plugin, or describes a new optional feature ("add a polls feature") even without saying "module". Also use when a module exists but is not showing up in the app or CI.
inclusion: manual
---

# New VERA Android Feature Module

Invoke with `/new-feature-module` in Claude Code, or `#vera-new-module` in kiro.

There is no scaffolding script in this repo; the template is an existing module. `vonage-feature-reactions` is the smallest complete example (contract + signal plugin + UI, both flavors, `testEnabled`); `vonage-feature-captions` shows a flavor that needs an extra dependency only in `enabled`; `vonage-feature-okta` shows a flavor that changes `minSdk`.

## Before writing files — decide with the user

1. **Name**: module `vonage-feature-<name>` (kebab-case), namespace `com.vonage.android.<name>`, flavor dimension `<name>`, config key `meetingRoomSettings.allow<Name>` (or another group if it belongs there), `MeetingRoomFeature.<NAME>`.
2. **Does it need to be optional at compile time?** Yes for anything that pulls a dependency or a permission a deployment might not want. Then it gets `enabled`/`disabled` flavors. If it is always-on, it is not a feature module — put it in `vonage-meeting-room` or `vonage-video-ui-compose` instead.
3. **Does it talk over the session?** Then it needs a `SignalPlugin` (`com.vonage.android.kotlin.signal.SignalPlugin` in `vonage-video-core`): `canHandle(type)`, `handleSignal(...)`, `sendSignal(...)`, `output: StateFlow`. Chat and reactions are the two existing ones.
4. **Where does it surface?** In-call (bottom bar button / overlay) → gated in `vonage-meeting-room` by `MeetingRoomFeature`. App-level (landing, waiting room, goodbye) → consumed from `app/` via Hilt, like `vonage-feature-okta`.

## Scaffold

```
vonage-feature-<name>/
├── build.gradle.kts            # copy from vonage-feature-reactions, change namespace + dimension
├── src/main/AndroidManifest.xml  # <manifest/> only (plus <uses-permission> if needed)
├── src/main/java/com/vonage/android/<name>/<Name>Feature.kt       # contract: interfaces only
├── src/enabled/java/com/vonage/android/<name>/di/<Name>Module.kt   # object <Name>Module { fun provide<Name>(...): <Contract> = Enabled… }
├── src/enabled/java/com/vonage/android/<name>/Enabled<Name>….kt    # real implementation + UI
├── src/disabled/java/com/vonage/android/<name>/di/<Name>Module.kt  # SAME signatures, returns Disabled… no-ops
├── src/disabled/java/com/vonage/android/<name>/Disabled<Name>….kt  # no-op implementation; UI composables render nothing
└── src/testEnabled/java/com/vonage/android/<name>/…Test.kt         # JUnit 5 + MockK + Turbine
```

Rules that keep both flavors compiling against the same callers:

- `src/main` holds only what callers reference: interfaces, data classes, the `*TestTags` object if any. No implementation.
- The provider object (`<Name>Module`) has **identical signatures** in `enabled` and `disabled` — callers (`MeetingRoomContainer`, `FeaturesModule`) compile against whichever flavor is selected and never know which. If `enabled` needs a dependency the stub ignores (Retrofit for captions), keep the parameter in both and declare the dep with `implementation`, using `enabledImplementation` only for libraries the contract doesn't expose (see captions' `build.gradle.kts`).
- Composables in `disabled` keep the same signature and emit nothing, so call sites don't need `if` checks.
- Any manifest additions that only make sense when enabled (permissions, activities, intent filters) go in `src/enabled/AndroidManifest.xml`, like okta.
- Tests for the real implementation live in `src/testEnabled` (there is nothing worth testing in `disabled`). Run them with `./gradlew :vonage-feature-<name>:testEnabledDebugUnitTest`.
- Build file essentials: `alias(libs.plugins.android.library)`, `kotlin.compose` if it has UI, `compileSdk`/`minSdk` from `libs.versions`, Java 17, `testOptions.unitTests.all { it.useJUnitPlatform() }`, and
  ```kotlin
  flavorDimensions += "<name>"
  productFlavors { create("enabled") { dimension = "<name>" }; create("disabled") { dimension = "<name>" } }
  ```
- Depend on `vonage-video-core` / `vonage-video-shared` / `vonage-video-ui-compose` abstractions; link `libs.vonage.android.sdk` only when a type from it is unavoidable. Never introduce Hilt into a feature module — `vonage-meeting-room` consumes them without Hilt.

## Integration checklist (a module that skips these compiles but never appears)

1. **`settings.gradle.kts`** — `include(":vonage-feature-<name>")`.
2. **Config key** — follow the feature-flag guide (`/feature-flag`, `#vera-feature-flags`): JSON key, `Config.kt` + `GetConfigTest`, `docs/CONFIGURATION.md`.
3. **`app/build.gradle.kts`** — `implementation(project(":vonage-feature-<name>"))` and, in `defaultConfig`, the property read + `buildConfigField("boolean", "FEATURE_<NAME>_ENABLED", …)` + `missingDimensionStrategy("<name>", prop.toEnabledString())`. Without the strategy the build fails with "Could not determine the dependencies of … dimension '<name>'".
4. **`vonage-meeting-room/build.gradle.kts`** — `implementation(project(":vonage-feature-<name>"))` and `missingDimensionStrategy("<name>", "enabled")` in `defaultConfig`. Do **not** add a new `flavorDimensions` entry unless the meeting-room module itself has flavor-specific source sets for it — every extra real dimension doubles its 16 variants and the test time with them. The runtime `MeetingRoomFeature` filter is what turns the feature off for hosts.
5. **`vonage-meeting-room-sample-app/build.gradle.kts`** — `missingDimensionStrategy("<name>", "enabled")`.
6. **`MeetingRoomContainer.kt`** (`vonage-meeting-room/src/main/java/com/vonage/android/meetingroom/internal/container/`, manual DI, lazy properties) — add `private val <name>… by lazy { <Name>Module.provide<Name>(...) }`; signal plugins join `signalPlugins = listOfNotNull(chatSignalPlugin, reactionSignalPlugin, …)` in `videoClient`.
7. **Runtime gate** — add `MeetingRoomFeature.<NAME>` in `vonage-meeting-room/.../api/MeetingRoomFeature.kt` (public API, so document the KDoc) and gate the UI with `MeetingRoomFeature.<NAME> in enabledFeatures` exactly where chat/reactions are gated: bottom-bar visibility in `internal/screen/MeetingRoomScreen.kt` (`BottomBarActionType` lives in `vonage-video-ui-compose/.../components/bottombar/BottomBarAction.kt`) and the overlay/sheet in `components/bottombar/BottomBar.kt`. Then map the config key in `app/.../screen/room/MeetingRoomScreenRoute.kt` `configuredMeetingRoomFeatures()`.
8. **App-side Hilt (only if `app/` uses it directly)** — `app/src/main/java/com/vonage/android/di/FeaturesModule.kt` `@Provides` delegating to `<Name>Module`, and `SdkModule.provideVonageVideoClient(...)` if it is a signal plugin the app's own client must know about.
9. **Docs** — module map in `AGENTS.md`, feature list in `README.md`, `docs/CONFIGURATION.md`.
10. **Verify both flavors** —
    ```bash
    ./gradlew :vonage-feature-<name>:testEnabledDebugUnitTest
    ./gradlew generateVonageConfig :app:assembleDebug            # key true
    # flip the key to false in app-config.json
    ./gradlew generateVonageConfig :app:assembleDebug            # key false → disabled source set must compile too
    ./gradlew detekt                                              # strict Compose rules; the pre-push hook runs this
    ```
    Kover coverage is applied to every subproject automatically (root `build.gradle.kts`), and CI gates on coverage of new code — write the `testEnabled` tests before opening the PR.

## Conventions to hold the line on

- One concern per PR (`docs/CONTRIBUTING.md`): scaffold + contract first, then the enabled implementation, then UI wiring is a reasonable split.
- TestTags for the new UI go in a `*TestTags` object in the module (`src/main` so instrumented/Maestro tests can reference it), kebab-case ids aligned with iOS (`room-name-input` style), `.testTag()` first in the modifier chain.
- Public surface of `vonage-meeting-room` is the `api/` package only; everything else is `internal`. If the host app needs to react to the feature, add a `MeetingRoomSDKAction` case rather than exposing internals.
- `vonage-android-logger` and `vonage-audio-selector` have binary-API dumps; if your module changes their public API, run `./gradlew <module>:androidApiDump` and commit the `.api` file.
