---
name: record-snapshots
description: Re-record or add Roborazzi (Robolectric) snapshot test goldens for vonage-video-ui-compose in the Vonage Video Android reference app (VERA) the right way — rule out git-lfs pointer files, recordRoborazziDebug, verifyRoborazziDebug, review the PNG diffs, commit the LFS-tracked goldens. Use whenever snapshot or screenshot tests fail with image diffs, the user changed a Compose component's visuals or theme.json and needs new baselines, mentions goldens, reference images, record mode, *_compare.png, or wants a new *ScreenshotTest written.
inclusion: fileMatch
fileMatchPattern: 'vonage-video-ui-compose/src/test/**'
---

# Snapshot Tests (Roborazzi) — Record, Verify, Review

In kiro this attaches when a file under `vonage-video-ui-compose/src/test/` is in context; otherwise `#vera-snapshot-tests`. General test routing is `/run-tests` (`#vera-testing`).

Snapshot tests run on the JVM (Robolectric, `GraphicsMode.NATIVE`) — no device. Goldens are committed PNGs in `vonage-video-ui-compose/src/test/snapshots/images/`, named `<package>.<TestClass>.<testMethod>.png`, and **LFS-tracked** via `.gitattributes`. Comparison uses `SnapshotTestDefaults.OPTIONS` (1 % change threshold) so goldens recorded on macOS verify on the Linux CI runner; the older per-test `changeThreshold = 0f` snippet in `.github/instructions/snapshot-tests.instructions.md` is stale — reuse the shared defaults.

## Step 0: Rule out a fake failure

- **LFS pointers.** On a clone made without git-lfs, every golden is a short text file and *all* tests fail with absurd diffs.
  ```bash
  head -c 60 "$(ls vonage-video-ui-compose/src/test/snapshots/images/*.png | head -1)"; echo
  ```
  `version https://git-lfs...` → `git lfs install && git lfs pull`, re-run verify. Never record over pointer files.
- **Everything fails after a `theme.json` change** → expected; that is a real re-record (see below).
- **Only one or two tests fail, in both light and dark** → a real component change; proceed.
- **A single test fails by a hair after a Robolectric/Compose bump** → compare the `*_compare.png`; if the delta is anti-aliasing, re-record just that class.

## Step 1: Record

There is no record flag to flip in code; recording is a separate Gradle task that overwrites the goldens for whatever tests run:

```bash
./gradlew :vonage-video-ui-compose:recordRoborazziDebug                                  # everything
./gradlew :vonage-video-ui-compose:recordRoborazziDebug --tests '*VonageButtonScreenshotTest'   # one class
```

Narrow to the classes you changed — recording everything after a one-component change hides unrelated drift in the review step.

## Step 2: Verify

```bash
./gradlew :vonage-video-ui-compose:verifyRoborazziDebug
```

Must be green. On failure Roborazzi writes side-by-side `*_compare.png` images (expected | diff | actual) under `vonage-video-ui-compose/build/outputs/roborazzi/` and lists them in the HTML test report.

## Step 3: Review and commit

1. `git status vonage-video-ui-compose/src/test/snapshots/images/` — the changed set should match the components you touched. Unexpected files = side effect to flag, not commit.
2. Open the changed PNGs (the Read tool renders images) and confirm the new baseline shows the intended change and nothing else.
3. Renamed/removed a test method? Roborazzi doesn't delete the old golden — remove the orphaned PNG by hand.
4. Commit the PNGs with the code change. They are LFS objects automatically (`.gitattributes`); the pre-push hook runs `git lfs pre-push`, so git-lfs must be installed to push.

## Adding a new snapshot test

Each component has a `@PreviewLightDark internal fun <Component>Preview()` in its **source** file (`src/main`, wrapped in `VonageVideoTheme`) and a `<Component>ScreenshotTest` in `src/test` that renders that preview. Copy an existing test (`VonageButtonScreenshotTest.kt`) rather than the doc snippet:

```kotlin
@OptIn(ExperimentalRoborazziApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class MyComponentScreenshotTest {
    @get:Rule val composeTestRule = createComposeRule()      // import from androidx.compose.ui.test.junit4.v2 like the neighbours
    private val options = SnapshotTestDefaults.OPTIONS

    @Test fun myComponent_light() {
        composeTestRule.setContent { MyComponentPreview() }
        composeTestRule.onRoot().captureRoboImage(roborazziOptions = options)
    }

    @Test @Config(qualifiers = "+night") fun myComponent_dark() { /* same body */ }
}
```

- Naming: `<component>[<Variant>]_light` / `_dark` (`vonageButtonDisabled_dark`).
- Dialogs/popups create a second composition root: capture `composeTestRule.onAllNodes(isRoot())[1]` instead of `onRoot()`.
- JUnit 4 here (`org.junit.Test`), unlike the JUnit 5 unit tests elsewhere.
- Then `recordRoborazziDebug --tests '*MyComponentScreenshotTest'`, review, commit.

## Theme changes

`theme.json` → `./gradlew updateTheme` → most goldens legitimately change. Record everything, then scan the diff for anything that changed *more* than the colour/typography you edited (layout shifts mean a `Dimen`/`Shape` side effect). Expect a large LFS commit; mention it in the PR description.
