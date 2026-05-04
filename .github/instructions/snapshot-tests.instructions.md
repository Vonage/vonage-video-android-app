---
applyTo: "vonage-video-ui-compose/src/test/**/*.kt"
---

# Snapshot Tests

This project uses **Roborazzi** (backed by Robolectric) for JVM snapshot tests of Compose UI components in the `vonage-video-ui-compose` module. Snapshot tests run locally without a device.

## Architecture Overview

Each UI component under test has two parts:

1. **`@PreviewLightDark` function** — defined in the **source** file (`src/main`), annotated `internal`, wraps the composable in `VonageVideoTheme` and provides representative data.
2. **`*ScreenshotTest`** — in `src/test`, calls the preview function and captures the image with Roborazzi.

Golden images are stored in `vonage-video-ui-compose/src/test/snapshots/images/` (configured via `roborazzi { outputDir }` in `build.gradle.kts`).

---

## 1. Adding a Preview Function (Source Module)

Define an `internal` `@PreviewLightDark` function at the bottom of the component's source file in `src/main`:

```kotlin
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.theme.VonageVideoTheme

@PreviewLightDark
@Composable
internal fun MyComponentPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(VonageVideoTheme.dimens.paddingDefault)
        ) {
            MyComponent(
                // representative, hardcoded data
            )
        }
    }
}
```

- Always wrap in `VonageVideoTheme`.
- Use `@PreviewLightDark` (not `@Preview`) so Android Studio renders both modes.
- One preview function per meaningful visual variant (e.g. empty state, filled state).

---

## 2. Screenshot Test Class

Located at: `vonage-video-ui-compose/src/test/java/com/vonage/android/compose/components/<MyComponent>ScreenshotTest.kt`

```kotlin
package com.vonage.android.compose.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MyComponentScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val options = RoborazziOptions(
        compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0f)
    )

    @Test
    fun myComponent_light() {
        composeTestRule.setContent { MyComponentPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test
    @Config(qualifiers = "+night")
    fun myComponent_dark() {
        composeTestRule.setContent { MyComponentPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }
}
```

### Required annotations

| Annotation | Purpose |
|---|---|
| `@OptIn(ExperimentalRoborazziApi::class)` | Required on class for `RoborazziOptions` |
| `@RunWith(RobolectricTestRunner::class)` | Runs on JVM with simulated Android |
| `@GraphicsMode(GraphicsMode.Mode.NATIVE)` | Enables native rendering pipeline for accurate Compose output |
| `@Config(qualifiers = "+night")` | On dark-mode test methods only |

---

## 3. Dialogs and Multi-Root Composables

`AlertDialog` and similar composables create a **separate composition root** for the dialog window. Use `onAllNodes(isRoot())` with an index to target the dialog root:

```kotlin
import androidx.compose.ui.test.isRoot

// Index 0 is the empty main window; index 1 is the dialog root.
composeTestRule.onAllNodes(isRoot())[1].captureRoboImage(roborazziOptions = options)
```

---

## 4. Test Naming Convention

```
<componentName>_<theme>
```

Examples:
- `vonageButton_light` / `vonageButton_dark`
- `avatarInitials_light` / `avatarInitials_dark`
- `avatarInitialsEmpty_light` / `avatarInitialsEmpty_dark`

For multiple preview variants, prefix with the variant name:
- `myComponent_empty_light` / `myComponent_empty_dark`
- `myComponent_filled_light` / `myComponent_filled_dark`

---

## 5. Running Snapshot Tests

| Command | Description |
|---|---|
| `./gradlew :vonage-video-ui-compose:test` | Verify snapshots match golden images |
| `./gradlew :vonage-video-ui-compose:recordRoborazziDebug` | Record new / update golden images |
| `./gradlew :vonage-video-ui-compose:verifyRoborazziDebug` | Explicitly verify against goldens |

**Workflow when changing component visuals:**
1. Make the UI change.
2. Run `recordRoborazziDebug` to regenerate goldens.
3. Review the updated PNGs in `src/test/snapshots/images/`.
4. Commit the updated goldens alongside the code change.

---

## 6. File Location Conventions

```
Source (preview functions):
  vonage-video-ui-compose/src/main/java/com/vonage/android/compose/components/<MyComponent>.kt

Test class:
  vonage-video-ui-compose/src/test/java/com/vonage/android/compose/components/<MyComponent>ScreenshotTest.kt

Golden images (auto-generated, must be committed):
  vonage-video-ui-compose/src/test/snapshots/images/
    com.vonage.android.compose.components.<MyComponent>ScreenshotTest.<testName>.png
```

---

## 7. Checklist for New Snapshot Test

- [ ] Add `@PreviewLightDark internal fun <MyComponent>Preview()` to source file, wrapped in `VonageVideoTheme`
- [ ] Create `<MyComponent>ScreenshotTest.kt` in the matching test package
- [ ] Add `@RunWith(RobolectricTestRunner::class)` + `@GraphicsMode(NATIVE)` to the class
- [ ] Add `@OptIn(ExperimentalRoborazziApi::class)` to the class
- [ ] Write both `_light` and `_dark` test methods; add `@Config(qualifiers = "+night")` to the dark test
- [ ] Use `onAllNodes(isRoot())[1]` for dialogs instead of `onRoot()`
- [ ] Run `./gradlew :vonage-video-ui-compose:recordRoborazziDebug` to generate golden images
- [ ] Commit generated PNGs in `src/test/snapshots/images/`
- [ ] Run `./gradlew :vonage-video-ui-compose:test` to confirm tests pass
