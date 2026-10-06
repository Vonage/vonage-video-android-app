---
name: run-tests
description: Route "run the tests" to the right Gradle task for the Vonage Video Android reference app (VERA) — per-module unit tests, the single enabled-flavor variant for feature modules and vonage-meeting-room (instead of all 16 variants), Roborazzi snapshot verify, instrumented tests on a device or the Gradle Managed Device, Maestro E2E, detekt, the binary-API check, and the CI-equivalent gate. Use whenever the user asks to run tests, verify a change, check whether tests pass, run one test class or method, reproduce a CI failure, or after editing code in any module when tests should be run.
inclusion: fileMatch
fileMatchPattern: '**/src/test*/**'
---

# Running VERA Android Tests

In kiro this attaches when a unit-test file is in context; pull it in with `#vera-testing` for instrumented, snapshot or Maestro work.

Pick the narrowest task that covers the change. The naive `./gradlew test` builds and runs **every** flavor combination — `vonage-meeting-room` alone has 16 variants (archiving × captions × screensharing × debug/release) and each feature module runs its `disabled` variant too, where there are no tests. The per-variant tasks below give the same coverage in a fraction of the time and are what CI effectively exercises.

Prerequisite for anything under `:app:` — `BASE_API_URL` in `local.properties` or the env (`/setup`, `#vera-setup`). Other modules don't need it.

## Routing: changed file → task

| Sources in | Run | Notes |
|---|---|---|
| `app/src/main` or `app/src/test` | `./gradlew :app:testDebugUnitTest` | JUnit 5 (`useJUnitPlatform`), MockK, Turbine, `isReturnDefaultValues = true`. `:app:test` would also build release. |
| `vonage-video-core`, `vonage-video-shared`, `vonage-android-logger`, `vonage-audio-selector`, `vonage-video-ui-compose` (non-snapshot tests, e.g. `GridLayoutCalculatorTest`) | `./gradlew :<module>:testDebugUnitTest` | |
| `vonage-feature-*/src/{main,enabled,testEnabled}` | `./gradlew :vonage-feature-<x>:testEnabledDebugUnitTest` | Tests live in `src/testEnabled` only; `:…:test` additionally compiles/runs the empty `disabled` variant. |
| `vonage-meeting-room/src/{main,test}` | `./gradlew :vonage-meeting-room:testArchivingEnabledCaptionsEnabledScreensharingEnabledDebugUnitTest` | The all-enabled variant is the one the app ships with the default config. Run a `…Disabled…` variant only when you changed flavor-specific code. |
| `vonage-config-idea-plugin/src/test` | `./gradlew :vonage-config-idea-plugin:test` | Plain JVM module, **JDK 21 toolchain** required. |
| `vonage-video-ui-compose/src/test/**ScreenshotTest.kt` or any `src/main` Compose component there | `./gradlew :vonage-video-ui-compose:verifyRoborazziDebug` | Snapshot compare against committed goldens (1 % threshold). Failures → `/record-snapshots` (`#vera-snapshot-tests`). |
| `app/src/androidTest` | `./gradlew :app:pixelDebugAndroidTest` (Gradle Managed Device, what CI runs) or `./gradlew :app:connectedDebugAndroidTest` (attached device/emulator) | Hilt + Compose UI tests, JUnit 4, `HiltTestRunner`. |
| `vonage-meeting-room/src/androidTest` | `./gradlew :vonage-meeting-room:connectedArchivingEnabledCaptionsEnabledScreensharingEnabledDebugAndroidTest` | No managed device is defined for this module; needs a connected device. |
| `build-tools/gradle-config-plugin` | `./gradlew generateVonageConfig` then `:app:testDebugUnitTest --tests '*GetConfigTest'` | The plugin has no unit tests; the app's `GetConfigTest` pins the generated mapping. |
| `.maestro/flows/*.yaml`, test tags, screen routes | `./scripts/run_maestro_tests.sh [flow.yaml]` | See E2E below. |

A change to a `vonage-video-core` interface also needs the consumers: `vonage-meeting-room` (all-enabled variant) and `:app:testDebugUnitTest`. When unsure, run the CI gate.

## Narrowing

```bash
./gradlew :app:testDebugUnitTest --tests 'com.vonage.android.config.GetConfigTest'
./gradlew :app:testDebugUnitTest --tests '*WaitingRoomViewModelTest.given_*'   # pattern on method names
./gradlew :vonage-video-ui-compose:verifyRoborazziDebug --tests '*VonageButtonScreenshotTest'
```

Unit tests mostly use JUnit 5 (`org.junit.jupiter.api.Test`, backtick names or `given_<precondition>_THEN_<outcome>`); instrumented and Roborazzi tests use JUnit 4. Mixing the annotations is the usual cause of "no tests found".

## CI-equivalent gate (what a PR must pass)

```bash
./gradlew clean koverXmlReportDebug detekt                          # unit tests of every kover-included module + coverage XML + static analysis
./gradlew :vonage-video-ui-compose:verifyRoborazziDebug             # snapshot goldens (CI checks out with lfs: true)
./gradlew vonage-android-logger:androidApiCheck vonage-audio-selector:androidApiCheck   # binary API dumps
./gradlew :app:pixelDebugAndroidTest --no-build-cache               # UI tests job (needs KVM/HAXM locally; slow)
```

`koverXmlReportDebug` runs the debug unit-test tasks of every module except `vonage-video-ui-compose`, `vonage-video-sdk` and `vonage-config-idea-plugin` (excluded in root `build.gradle.kts`), so the snapshot module and the IDE plugin must be run separately as above. The pre-push hook (`./gradlew installGitHooks`) runs `detekt` and `git lfs pre-push` — a detekt finding blocks the push locally before CI sees it.

`androidApiCheck`/`androidApiDump` are the task names here; the stock `apiCheck`/`apiDump` from the binary-compatibility-validator plugin are not registered under AGP 9 (see the workaround comment in those modules' `build.gradle.kts`). After an intentional public-API change: `./gradlew <module>:androidApiDump` and commit the `.api` file.

## E2E (Maestro)

```bash
./scripts/install_maestro.sh                           # once: Maestro CLI, checks Java 17 + Android SDK
./scripts/run_maestro_tests.sh                         # builds debug APK, boots first AVD, runs all flows
./scripts/run_maestro_tests.sh create-new-room.yaml    # single flow
./scripts/run_maestro_tests.sh --avd Pixel_4_API_34
maestro test --env APP_ID=com.vonage.android.debug --env ROOM_NAME=local-$RANDOM .maestro/flows/create-new-room.yaml
```

Flows need a reachable backend (`BASE_API_URL`) and a real emulator; CI (`.github/workflows/maestro.yml`) runs them against the **release** APK with `APP_ID=com.vonage.android` and pre-grants camera/mic/Bluetooth/notification permissions with `adb shell pm grant`. Flows target Compose `testTag`s exposed as resource ids (`testTagsAsResourceId`), kebab-case ids shared with iOS; `*.yaml.disabled` flows are skipped. Flow list: `.maestro/TESTS.md`.

## Output handling

Gradle prints only failures. Always read and report the real summary — `BUILD SUCCESSFUL`/`FAILED` plus the failing test names — never infer a pass from filtered output. Reports:

- unit: `<module>/build/reports/tests/<task>/index.html`, XML in `<module>/build/test-results/<task>/`
- instrumented: `app/build/reports/androidTests/`, `app/build/outputs/androidTest-results/`
- snapshot diffs: `vonage-video-ui-compose/build/outputs/roborazzi/` (`*_compare.png`)
- coverage: `./gradlew koverHtmlReport` → `build/reports/kover/html/index.html`

Useful filter when the log is long:
```bash
./gradlew :app:testDebugUnitTest 2>&1 | grep -E "FAILED|PASSED|tests completed|BUILD" | tail -20
```

## Gotchas

- `./gradlew test` at the root also hits `:vonage-config-idea-plugin:test`, which needs a JDK 21 toolchain; skip with `-x :vonage-config-idea-plugin:test` when only Android modules matter.
- A fresh clone without git-lfs has pointer-file goldens: every snapshot test fails. `git lfs pull` first.
- Managed-device runs (`pixelDebugAndroidTest`) download an `aosp-atd` API 34 image on first use and need hardware acceleration; on CI the job is 45 min. Prefer `connectedDebugAndroidTest` against an already-running emulator locally.
- Instrumented conventions (`docs/TESTING.md`): ScreenObject wrapping `SemanticsNodeInteractionsProvider` + `@HiltAndroidTest @RunWith(AndroidJUnit4::class)` ScreenTest, `HiltAndroidRule` at `order = 0`, `GrantPermissionRule` at `order = 1` if needed, compose rule last, `setContent` wrapped in `VonageVideoTheme`, `assertDoesNotExist()` for absent nodes vs `assertIsNotDisplayed()` for off-screen ones.
- Release unit-test variants pull in the Google Services plugin only when the task name contains "Release"; stick to `*Debug*` tasks locally unless you have `app/src/release/google-services.json`.
