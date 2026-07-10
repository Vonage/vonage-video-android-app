# VIDSOL-946: [Android] Hide top and bottom bars on meeting room

## Ticket

- **Project**: VIDSOL
- **Priority**: P3
- **Points**: 2
- **Sprint**: VIDSOL 26'3-S1 (active)
- **Epic**: VPF-1403

## Goal

When tapping the video tiles layout in the meeting room, toggle the visibility of the top and bottom bars to give more screen space to video streams. iOS app is the reference implementation (animated `easeInOut` toggle).

## Implementation

**Single file changed:** `vonage-meeting-room/src/main/java/com/vonage/android/meetingroom/internal/screen/MeetingRoomScreen.kt`

### Approach

Bar visibility is purely UI state — no ViewModel involvement needed. Local Compose state (`remember { mutableStateOf(true) }`) is sufficient since the bars default to visible and there is no need to survive process death.

### Changes

1. **Bars visibility state** — added `var showBars by remember { mutableStateOf(true) }` at the top of `MeetingRoomScreen`.

2. **Tap gesture** — added `pointerInput(Unit) { detectTapGestures(onTap = { showBars = !showBars }) }` on the main pane `Box` that wraps the video tiles, overlays, and `MeetingRoomContent`.

3. **Top bar animation** — wrapped `MeetingTopBar` in `AnimatedVisibility` with `slideInVertically(initialOffsetY = { -it })` / `slideOutVertically(targetOffsetY = { -it })` using a 300ms `tween`.

4. **Bottom bar animation** — wrapped `BottomBar` in `AnimatedVisibility` with `slideInVertically(initialOffsetY = { it })` / `slideOutVertically(targetOffsetY = { it })` using a 300ms `tween`.

5. **Constant** — added `private const val BAR_TOGGLE_DURATION_MS = 300` at file scope.

### Key decisions

- **Local state over ViewModel state**: bars visibility is ephemeral UI chrome, not domain state. No need to expose it via `MeetingRoomUiState`.
- **`pointerInput` on the container Box**: sits below interactive children (buttons, cards) in the pointer routing tree, so existing long-press on `ParticipantVideoCard` and bottom sheet triggers are unaffected.
- **`slideIn/OutVertically`**: matches the iOS directional animation — top bar slides toward the top edge, bottom bar slides toward the bottom edge.

## Verification

- `assembleDebug` passed across all 8 flavor variants (archiving × captions × screensharing).
- `detekt` passed clean.
- Pre-existing test failure (`CallLayoutType.GRID` unresolved reference in `MeetingRoomViewModelTest.kt:301`) confirmed present before this change — not introduced by it.
