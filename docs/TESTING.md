# Testing

## Unit and Instrumented Tests

This project uses a combination of frameworks for comprehensive test coverage:

- **Unit tests**: JUnit 4/5, MockK, Turbine (Flow testing), Kotlinx Coroutines Test, and AndroidX Core Testing
- **UI / Instrumented tests**: Compose UI Test, Espresso, Hilt Testing, and AndroidX Test Runner/Rules
- **Coverage**: Kover for code coverage reports, integrated with SonarQube

Unit tests are spread across multiple modules (`app`, `vonage-video-core`, `vonage-audio-selector`, `vonage-video-shared`, `vonage-android-logger`, `vonage-feature-chat`, and `vonage-config-idea-plugin`). Instrumented tests live in the `app` module and follow a Page Object / Screen Object pattern with a custom `HiltTestRunner` for dependency injection support.

### Running tests

```bash
# Run all unit tests
./gradlew test

# Run tests for a specific module
./gradlew :vonage-video-core:test

# Run instrumented tests (requires connected device/emulator)
./gradlew connectedAndroidTest

# Run instrumented tests on Gradle Managed Devices (no physical device needed)
./gradlew pixelDebugAndroidTest

# Generate code coverage report
./gradlew koverHtmlReport

# CI-equivalent quality gate (tests + coverage XML + static analysis)
./gradlew clean koverXmlReportDebug detekt
```

Or run tests in Android Studio by right-clicking on test files or packages and selecting **Run Tests**.

### Snapshot tests (`vonage-video-ui-compose`)

Snapshot tests use [Roborazzi](https://github.com/takahirom/roborazzi) (Robolectric-backed) and live in `vonage-video-ui-compose/src/test/`. Golden PNGs are committed at `vonage-video-ui-compose/src/test/snapshots/images/`.

When changing a component's visuals:

1. Make the UI change.
2. Regenerate golden images: `./gradlew :vonage-video-ui-compose:recordRoborazziDebug`
3. Review the updated PNGs, then commit them alongside the code.

To verify against committed goldens (CI-equivalent):

```bash
./gradlew :vonage-video-ui-compose:verifyRoborazziDebug
```

### Instrumented test conventions

- Pattern: **ScreenObject** (wraps `SemanticsNodeInteractionsProvider`) + **ScreenTest** (`@HiltAndroidTest @RunWith(AndroidJUnit4::class)`)
- TestTag constants go in the **source** `*Route.kt` file as an `object <Screen>TestTags`. Apply `.testTag(TAG)` **first** in the modifier chain.
- Rule ordering: `HiltAndroidRule` at `order=0`; if permissions needed, `GrantPermissionRule` at `order=1`; `createComposeRule` last.
- Always wrap `setContent` in `VonageVideoTheme { ... }`.
- Test naming: `given_<precondition>_THEN_<expected_outcome>`.
- Use `assertDoesNotExist()` when a node is absent from composition; `assertIsNotDisplayed()` when it exists but is off-screen.
- Screen actions data class: all lambda fields must default to `{}` (no-op).

---

## E2E Testing with Maestro

This project uses [Maestro](https://maestro.mobile.dev) for end-to-end UI testing. Tests are YAML-based and interact with the app through Compose test tags exposed as resource IDs.

> The canonical Maestro README lives at [`.maestro/README.md`](../.maestro/README.md). The content below mirrors it for discoverability.

### Installation

Use the provided install script (installs Maestro CLI + verifies Java 17 and Android SDK):

```bash
./scripts/install_maestro.sh
```

### Folder structure

```
.maestro/
├── config.yaml              # Global configuration (appId, platform)
└── flows/                   # Test flows (YAML files)
    ├── create-new-room.yaml
    ├── github-repo-link.yaml
    ├── goodbye-view-landing-page.yaml
    ├── join-with-camera-mic-allowed.yaml
    ├── waiting-room-controls-disabled.yaml
    └── waiting-room-controls-enabled.yaml
```

### Running tests

```bash
# Run all tests (builds APK, launches emulator, installs, runs flows)
./scripts/run_maestro_tests.sh

# Run a single flow by name
./scripts/run_maestro_tests.sh create-new-room.yaml

# Auto-launch first available emulator
./scripts/run_maestro_tests.sh --auto-emulator

# Use a specific AVD
./scripts/run_maestro_tests.sh --avd Pixel_4_API_34

# Run a single flow directly with Maestro CLI
maestro test --env APP_ID=com.vonage.android.debug .maestro/flows/create-new-room.yaml
```

### Test flows

Test flows are located in `.maestro/flows/`. See [`.maestro/TESTS.md`](../.maestro/TESTS.md) for a detailed list of all available flows.

Flows with the `.yaml.disabled` extension are skipped by Maestro — to re-enable, remove the `.disabled` suffix.

### Writing tests

#### APP_ID configuration

All flows use `appId: ${APP_ID}` which is resolved at runtime. The `run_maestro_tests.sh` script sets `APP_ID=com.vonage.android.debug` automatically. In CI, it is passed via `--env APP_ID=com.vonage.android.debug`.

#### Test tags

Tests target elements using Compose `testTag` modifiers exposed as resource IDs via `testTagsAsResourceId = true` in `MainActivity`. Test tags are defined in `*TestTags` objects within each screen's route file.

**Landing Screen** (`LandingScreenTestTags`):
- `landing-screen` — Screen container
- `landing-screen-title` — Title text
- `landing-screen-icon` — Vonage icon
- `landing-screen-subtitle` — Subtitle text
- `landing-screen-create-room-button` — Create room button
- `join-waiting-room-button` — Join room button
- `room-name-input` — Room name text field
- `landing-screen-room-error-label` — Room name validation error
- `github-repo-button` — GitHub repository link button

**Waiting Room** (`WaitingRoomTestTags`):
- `waiting-room-screen` — Screen container
- `join-meeting-button` — Join meeting button
- `username-input` — Username text field
- `waiting-room-mic-enabled` / `waiting-room-mic-disabled` — Mic toggle (dynamic)
- `waiting-room-camera-enabled` / `waiting-room-camera-disabled` — Camera toggle (dynamic)

**Meeting Room** (`BottomBarTestTags` / `MeetingRoomScreenTestTags`):
- `meeting-room-screen` — Screen container
- `meeting-room-end-call-button` — End call button
- `meeting-room-mic-enabled` / `meeting-room-mic-disabled` — Mic toggle (dynamic)
- `meeting-room-camera-enabled` / `meeting-room-camera-disabled` — Camera toggle (dynamic)

**Goodbye Screen** (`GoodbyeScreenTestTags`):
- `goodbye-screen` — Screen container
- `goodbye-reenter-button` — Re-enter meeting button
- `goodbye-landing-page-button` — Go to landing page button

#### Dynamic test tags

Mic and camera buttons use dynamic suffixes (`-enabled` / `-disabled`) based on their state, generated by `buildTestTag()` in `app/src/main/java/com/vonage/android/util/buildTestTag.kt`.

#### Defining test tags

```kotlin
object LandingScreenTestTags {
    const val LANDING_SCREEN_TAG = "landing-screen"
    const val CREATE_ROOM_BUTTON_TAG = "landing-screen-create-room-button"
    const val JOIN_BUTTON_TAG = "join-waiting-room-button"
    const val ROOM_INPUT_TAG = "room-name-input"
}
```

#### Applying test tags to composables

```kotlin
// Screen container
TwoPaneScaffold(
    modifier = modifier
        .fillMaxSize()
        .testTag(LANDING_SCREEN_TAG),
)

// Buttons
VonageButton(
    text = stringResource(R.string.landing_create_room),
    modifier = Modifier
        .fillMaxWidth()
        .testTag(CREATE_ROOM_BUTTON_TAG),
    onClick = actions.onCreateRoomClick,
)

// Text fields
VonageTextField(
    modifier = Modifier
        .fillMaxWidth()
        .testTag(ROOM_INPUT_TAG),
    value = roomName,
    onValueChange = actions.onRoomNameChange,
)
```

> Always apply `.testTag()` directly to the target composable, not to wrapper containers. The `testTagsAsResourceId` semantic property in the root `Box` ensures all test tags are exposed as `resource-id` values that Maestro can find.

Test tag IDs are aligned with the iOS codebase so the same Maestro YAML flows can be reused across platforms. Use kebab-case (e.g., `room-name-input`) and ensure consistency with the iOS accessibility identifiers.

### CI integration

Maestro tests run in GitHub Actions via `.github/workflows/maestro.yml`. The workflow:

1. Builds the debug APK
2. Launches an Android emulator (`aosp_atd`, API 34)
3. Installs the APK and runs all flows with `--env APP_ID=com.vonage.android.debug`
4. Outputs JUnit XML reports and uploads test artifacts

### Useful links

- [Maestro Documentation](https://maestro.mobile.dev)
- [Maestro CLI Commands](https://maestro.mobile.dev/api-reference/commands)
- [Maestro Studio (interactive recorder)](https://maestro.mobile.dev/getting-started/maestro-studio)
