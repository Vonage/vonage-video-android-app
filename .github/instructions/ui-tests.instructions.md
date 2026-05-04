---
applyTo: "app/src/androidTest/**/*.kt"
---

# UI Tests

This project uses Jetpack Compose instrumented UI tests with Hilt DI. Follow these conventions exactly when creating or modifying UI tests.

## Architecture Overview

Every screen under test has three parts:

1. **TestTags object** — string constants in the **source** module (`*Route.kt` or `*Screen.kt`), co-located with the screen
2. **ScreenObject** — in `androidTest`, wraps `SemanticsNodeInteractionsProvider`, exposes `val` properties per node
3. **ScreenTest** — `@HiltAndroidTest` instrumented test class

## 1. Adding TestTag Constants (Source Module)

Define an `object` in the screen's `*Route.kt` (or `*Screen.kt`) file, in the **same package** as the screen:

```kotlin
object MyScreenTestTags {
    const val MY_HEADER_TAG = "my_header"
    const val MY_BUTTON_TAG = "my_button"
    // naming: snake_case, descriptive, unique across app
}
```

Apply tags in composables using `.testTag()` at the **start** of the modifier chain:

```kotlin
import androidx.compose.ui.platform.testTag
import com.vonage.android.screen.myscreen.MyScreenTestTags.MY_HEADER_TAG

Text(
    text = "Hello",
    modifier = Modifier
        .testTag(MY_HEADER_TAG)  // testTag FIRST
        .padding(16.dp)
        .fillMaxWidth()
)
```

**Dynamic tags** (e.g. on/off states): suffix with `-on`/`-off`:
```kotlin
modifier = Modifier.testTag("$BUTTON_TAG-${if (enabled) "on" else "off"}")
```

## 2. ScreenObject

Located at: `app/src/androidTest/java/com/vonage/android/screen/<feature>/<Screen>Object.kt`

```kotlin
package com.vonage.android.screen.myscreen

import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag
import com.vonage.android.screen.myscreen.MyScreenTestTags.MY_BUTTON_TAG
import com.vonage.android.screen.myscreen.MyScreenTestTags.MY_HEADER_TAG

class MyScreenObject(compose: SemanticsNodeInteractionsProvider) {
    val header = compose.onNodeWithTag(MY_HEADER_TAG)
    val button = compose.onNodeWithTag(MY_BUTTON_TAG)
    // Use useUnmergedTree = true for error labels, nested text inside inputs:
    val inputError = compose.onNodeWithTag(MY_INPUT_ERROR_TAG, useUnmergedTree = true)
}
```

- No base class — direct `SemanticsNodeInteractionsProvider` calls
- For complex elements (top bars, bottom bars), use `ComposeTestElement` subclasses from `com.vonage.android.util`

## 3. ScreenTest Class Structure

Located at: `app/src/androidTest/java/com/vonage/android/screen/<feature>/<Screen>Test.kt`

```kotlin
package com.vonage.android.screen.myscreen

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vonage.android.compose.theme.VonageVideoTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class MyScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // If screen requires camera/mic: add GrantPermissionRule at order = 1, compose at order = 2
    @get:Rule(order = 1)
    val compose = createComposeRule()

    private val screen = MyScreenObject(compose)

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun given_initial_state_THEN_components_are_displayed() {
        compose.setContent {
            VonageVideoTheme {
                MyScreen(
                    uiState = MyScreenUiState(...),
                    actions = MyScreenActions(),
                )
            }
        }
        screen.header.assertIsDisplayed()
    }
}
```

### Rules ordering

| Scenario | order=0 | order=1 | order=2 |
|---|---|---|---|
| No permissions needed | `HiltAndroidRule` | `createComposeRule` | — |
| Camera/mic needed | `HiltAndroidRule` | `GrantPermissionRule` | `createComposeRule` |

```kotlin
// Permissions example (WaitingRoom):
@get:Rule(order = 1)
var runtimePermissionRule: GrantPermissionRule = GrantPermissionRule
    .grant(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)

@get:Rule(order = 2)
val compose = createComposeRule()
```

## 4. Test Naming Convention

```
given_<precondition>_THEN_<expected_outcome>
```

Examples:
- `given_initial_state_THEN_components_are_displayed`
- `given_join_button_clicked_THEN_onJoinRoom_callback_invoked`
- `given_state_without_video_THEN_components_are_displayed`

## 5. Test Types

### Display tests — verify UI presence/absence

```kotlin
@Test
fun given_idle_state_THEN_header_displayed_and_archives_absent() {
    compose.setContent {
        VonageVideoTheme {
            MyScreen(uiState = MyScreenUiState.Idle, actions = MyScreenActions())
        }
    }

    screen.header.assertIsDisplayed()
    screen.archivesContainer.assertDoesNotExist()  // node absent from composition entirely
    screen.disabledButton.assertIsNotDisplayed()   // node present but not visible
}
```

Use `assertDoesNotExist()` when the node is not in the composition at all (conditional rendering).
Use `assertIsNotDisplayed()` when the node exists but is hidden/off-screen.

### Callback/interaction tests — verify lambdas fire

```kotlin
@Test
fun given_button_clicked_THEN_callback_invoked() {
    var wasCalled = false

    compose.setContent {
        VonageVideoTheme {
            MyScreen(
                uiState = MyScreenUiState(...),
                actions = MyScreenActions(onButtonClick = { wasCalled = true }),
            )
        }
    }

    screen.button.performClick()
    assertTrue(wasCalled)
}
```

For callbacks with arguments:
```kotlin
var capturedArg: String? = null
actions = MyScreenActions(onInput = { capturedArg = it })
screen.input.performTextInput("hello")
assertTrue(capturedArg != null)
```

For scrollable content, chain performScrollTo() before interaction:
```kotlin
screen.joinButton.performScrollTo().performClick()
screen.roomInput.performScrollTo().performTextInput("room-name")
```

### State variant tests — verify different UI states render correctly

Create one test per meaningful state variant. Pass different `uiState` instances.

## 6. Actions and UiState

Screens follow the pattern:
- `*UiState` — sealed class or data class with state variants
- `*Actions` — data class with lambda fields, **all default to `{}`** (no-op)

```kotlin
// No-op actions — use when testing display only:
actions = MyScreenActions()

// Targeted override — use when testing a specific callback:
actions = MyScreenActions(onButtonClick = { wasCalled = true })

// Explicit all-args — use when default no-ops not available:
actions = MyScreenActions(
    onJoinRoomClick = {},
    onCreateRoomClick = { wasCalled = true },
    onRoomNameChange = {},
)
```

Extract a companion object constant for repeated no-op actions:
```kotlin
companion object {
    val NO_OP_ACTIONS = MyScreenActions(
        onJoinRoomClick = {},
        onCreateRoomClick = {},
        onRoomNameChange = {},
    )
}
```

## 7. Wrapping Theme

Always wrap composable under test in `VonageVideoTheme`:

```kotlin
compose.setContent {
    VonageVideoTheme {
        MyScreen(...)
    }
}
```

## 8. Complex Fake Objects in Tests

When a screen requires complex interface fakes (e.g. `PublisherParticipant`), create a private `@Composable` builder function at the bottom of the test class:

```kotlin
@Suppress("EmptyFunctionBlock")
@Composable
private fun buildPublisher(isMicEnabled: Boolean, isCameraEnabled: Boolean): PublisherParticipant {
    return object : PublisherParticipant {
        override val isMicEnabled: StateFlow<Boolean> = MutableStateFlow(isMicEnabled)
        override val isCameraEnabled: StateFlow<Boolean> = MutableStateFlow(isCameraEnabled)
        // ... all other required overrides
    }
}
```

For call/participant fakes, use helpers from `com.vonage.android.compose.preview`:
```kotlin
import com.vonage.android.compose.preview.buildCallWithParticipants

val call = buildCallWithParticipants(participantCount = 5, unreadCount = 0)
```

## 9. Archive Model (for tests involving archiving)

```kotlin
import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveStatus

val archive = Archive(
    id = ArchiveId("1"),
    name = "Recording 1",
    url = "url",
    status = ArchiveStatus.AVAILABLE,
    createdAt = 1231,
    duration = 123,
    size = 123123,
)
```

## 10. File Location Conventions

Source testTags:
  app/src/main/java/com/vonage/android/screen/<feature>/<Screen>Route.kt
  (append `object <Screen>TestTags { ... }` at end of file)

Test infrastructure:
  app/src/androidTest/java/com/vonage/android/screen/<feature>/<Screen>Object.kt
  app/src/androidTest/java/com/vonage/android/screen/<feature>/<Screen>Test.kt

Package in test files must match source package: `com.vonage.android.screen.<feature>`

## 11. Imports Reference

```kotlin
// Test assertions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextEquals

// Test interactions
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput

// Test infrastructure
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.SemanticsNodeInteractionsProvider
import androidx.compose.ui.test.onNodeWithTag

// Hilt
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest

// Assert
import org.junit.Assert.assertTrue

// Source modifier
import androidx.compose.ui.platform.testTag
```

## 12. Checklist for New Screen Test

- [ ] Add `object <Screen>TestTags` to source `*Route.kt`
- [ ] Apply `.testTag(TAG)` at start of modifier chain in each composable
- [ ] Create `<Screen>Object.kt` in test package with one `val` per tagged node
- [ ] Create `<Screen>Test.kt` with `@HiltAndroidTest @RunWith(AndroidJUnit4::class)`
- [ ] Add `HiltAndroidRule(order=0)` + `createComposeRule(order=last)` rules
- [ ] Add `@Before fun setup() { hiltRule.inject() }`
- [ ] Write display tests for each meaningful `uiState` variant
- [ ] Write callback tests for each interactive element
- [ ] Wrap all `setContent` in `VonageVideoTheme { ... }`
- [ ] Run `./gradlew detekt` to verify no lint issues
