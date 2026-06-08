# Plan: Fix Video Clipping in Active Speaker Layout

## Overview

When switching from Grid layout to Active Speaker layout in portrait orientation, the main participant's video displays with the left 50% showing as black and only the right 50% showing the right half of the video (cropped vertically down the middle). This is caused by the OpenTok native view retaining its cached dimensions from the smaller Grid tile when being reattached to the larger Active Speaker spotlight container.

The root cause is that `AndroidView.update` calls `container.addView(view)` without explicit `LayoutParams`, allowing the native view to retain its previous width (approximately half the spotlight width). The fix is to explicitly set `MATCH_PARENT` layout params when adding the view to force proper remeasurement.

## Branch name

`fix/active-speaker-video-clipping`

Run: `/worktree fix/active-speaker-video-clipping`

## Affected files

- `vonage-video-ui-compose/src/main/java/com/vonage/android/compose/components/VideoRenderer.kt` - Add explicit MATCH_PARENT layout params when adding OpenTok view to container

## Implementation steps

### 1. Add explicit layout params to VideoRenderer.kt

In the `ParticipantVideoRenderer` composable's `AndroidView.update` block:

**Current code (lines 26-32):**
```kotlin
update = { container ->
    container.removeAllViews()
    participant.view.let { view ->
        (view.parent as? ViewGroup)?.removeView(view)
        container.addView(view)
    }
}
```

**Change to:**
```kotlin
update = { container ->
    container.removeAllViews()
    participant.view.let { view ->
        (view.parent as? ViewGroup)?.removeView(view)
        container.addView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
    }
}
```

**Why this fixes the issue:**
- When the native OpenTok view is moved from a small Grid tile (~50% screen width) to the large Active Speaker spotlight (~100% screen width), it retains its cached dimensions
- Without explicit layout params, `addView(view)` preserves whatever layout params the view previously had
- Explicitly setting `MATCH_PARENT` forces the view to remeasure and fill the new container completely
- This ensures proper viewport sizing regardless of the previous container dimensions

### 2. Verify the fix doesn't break other scenarios

Test that video rendering still works correctly in:
- Grid layout (no regression)
- Active Speaker layout filmstrip thumbnails
- Screen sharing
- Camera on/off transitions
- Publisher (local) video
- Subscriber (remote) video

## Edge cases & risks

### Edge case 1: LayoutParams override existing view configuration
**Risk:** The OpenTok view might have internal layout params that should be preserved.

**Mitigation:** `MATCH_PARENT` is the standard Android pattern for child views in containers and matches the Compose `Modifier.fillMaxSize()` semantics already used. The OpenTok SDK's renderer style (`STYLE_VIDEO_SCALE` + `STYLE_VIDEO_FIT`) operates independently of layout params and controls how video is scaled/cropped within the view bounds.

### Edge case 2: Performance impact of forcing remeasurement
**Risk:** Explicitly setting layout params on every recomposition could cause unnecessary remeasurement.

**Mitigation:** The `update` block already runs on every recomposition where the container or participant changes. Adding explicit layout params is a negligible addition to this existing work. The alternative (leaving dimensions cached) causes visible bugs.

### Edge case 3: Landscape orientation
**Risk:** The fix might behave differently in landscape.

**Mitigation:** The issue doesn't occur in landscape (confirmed by testing), but the fix is orientation-agnostic. Setting `MATCH_PARENT` works correctly in both orientations.

### Edge case 4: Other layout transitions
**Risk:** There might be other layout transitions (e.g., Active Speaker → Grid, or transitions involving screen sharing) that exhibit similar issues.

**Mitigation:** According to the issue description, only Grid → Active Speaker in portrait shows the bug. Grid uses dynamically sized tiles that are generally similar in size to each other, so transitions within Grid don't trigger dramatic size changes. The fix is general-purpose and will prevent dimension-caching issues in any transition.

### Risk: Import required for FrameLayout.LayoutParams
**Risk:** Need to ensure `android.widget.FrameLayout` is imported.

**Mitigation:** `FrameLayout` is already imported at line 5 of `VideoRenderer.kt` for the factory. `LayoutParams` is an inner class, so no additional import needed.

## Testing

### Manual testing steps

1. **Reproduce the original bug:**
   - Start app in portrait orientation
   - Join a call with 2+ participants
   - Start in Grid layout
   - Switch to Active Speaker layout
   - **Expected (before fix):** Left 50% black, right 50% shows right half of video
   - **Expected (after fix):** Full video visible, center-cropped to fill spotlight

2. **Verify rotation behavior:**
   - Start in portrait Grid → switch to Active Speaker (verify no black bars)
   - Rotate to landscape → verify video still displays correctly
   - Rotate back to portrait → verify video still displays correctly

3. **Verify reverse transition:**
   - Start in portrait Active Speaker → switch to Grid
   - Verify Grid tiles display correctly (no regression)
   - Switch back to Active Speaker → verify no black bars

4. **Test with different participant counts:**
   - Repeat steps 1-3 with 2, 3, 4, and 5+ participants
   - Verify Grid tile sizes change correctly
   - Verify transitions still work

5. **Test publisher vs subscriber:**
   - Verify local publisher (your video) displays correctly in Active Speaker
   - Verify remote subscribers display correctly in Active Speaker
   - Test when you are the active speaker vs. someone else

6. **Test landscape Grid → Active Speaker:**
   - Start in landscape Grid
   - Switch to Active Speaker
   - Verify no clipping issues (issue doesn't occur in landscape but verify no regression)

### Unit/snapshot testing

The change is in a Compose UI component (`ParticipantVideoRenderer`). Existing snapshot tests in `vonage-video-ui-compose/src/test/` should continue to pass:

```bash
./gradlew :vonage-video-ui-compose:verifyRoborazziDebug
```

If snapshots change due to layout params affecting test rendering, regenerate them:

```bash
./gradlew :vonage-video-ui-compose:recordRoborazziDebug
```

Review the diffs carefully to ensure they represent correct behavior (full video visible, no clipping).

### Integration testing

Run instrumented tests to verify no regressions in video rendering:

```bash
./gradlew connectedAndroidTest
```

Specifically check tests in:
- `vonage-meeting-room/src/androidTest/` - Meeting room UI tests
- Any tests that exercise layout switching

### Verification checklist

- [ ] Original bug (portrait Grid → Active Speaker) is fixed
- [ ] No regression in Grid layout video display
- [ ] No regression in landscape Active Speaker
- [ ] Rotation works correctly (no black bars after rotation)
- [ ] Both publisher and subscriber videos display correctly
- [ ] All existing unit tests pass
- [ ] All existing instrumented tests pass
- [ ] Snapshot tests pass (or regenerated with correct output)

## Additional considerations

### Why rotation fixed the issue temporarily

Rotation triggers an Android configuration change that:
1. Destroys and recreates the entire Activity/Composition
2. All views are created from scratch with fresh measurement
3. The OpenTok native view has no cached dimensions from a previous container
4. Proper measurement sequence occurs naturally

This explained why the user observed that rotating to landscape and back to portrait would make the black bars "stay gone" — the view was recreated cleanly.

### Why Grid → Active Speaker but not the reverse

- **Grid → Active Speaker:** Small tile (~50% width) → Large spotlight (~100% width)
  - Native view retains small width, undersized in large container
  - Result: visible clipping bug
  
- **Active Speaker → Grid:** Large spotlight → Small tiles
  - Android automatically clips/scales oversized children
  - No visible issue (though the view might still be oversized, it's clipped correctly)

### Alternative solutions considered

**Option 2: Call `view.requestLayout()` after `addView`**
- Less explicit about intent
- `requestLayout()` is a hint, not guaranteed to trigger remeasurement
- Could have timing issues

**Option 3: Add `key` parameter to `AndroidView`**
- Forces complete recreation on modifier change
- Heavyweight solution (destroys and recreates views unnecessarily)
- Performance overhead
- Could disrupt video stream continuity

**Option 4: Use `ViewTreeObserver.OnGlobalLayoutListener`**
- Overly complex for this issue
- Potential performance impact from listener overhead
- Harder to maintain

**Selected solution (Option 1) is superior because:**
- Explicit and clear intent
- Standard Android pattern
- Minimal code change
- No performance overhead beyond existing recomposition
- Guarantees correct measurement

### Related issues from codebase history

From `PLAN.md` (previous video state bugs):
- **VIDSOL-824 (commit 7598f41c):** Video state corruption when scrolling off-screen
- **commit 1f2b4ac2:** Publisher video frozen after layout switch
- **commits 10b2d535, f25c89bc:** Background image cropping issues

This new issue is distinct — it's a **layout measurement/viewport clipping bug**, not a state management bug. The OpenTok renderer style (`STYLE_VIDEO_SCALE` + `STYLE_VIDEO_FIT`) is correctly configured; the problem is that the native view's dimensions are cached from a previous container.

### Why this wasn't caught earlier

1. **The issue is subtle:** Only occurs in one specific transition (Grid → Active Speaker) in one orientation (portrait)
2. **Rotation masks it:** If developers tested by rotating during development, the bug would fix itself
3. **Layout switching might be less common:** Users might join directly in one layout and stay there
4. **Recent architectural changes:** The extraction of `AdaptiveGrid` and `ActiveSpeakerLayout` into separate composables (commit 892b151b) might have changed recomposition behavior

### Documentation opportunity

Consider adding a comment in `VideoRenderer.kt` explaining why explicit layout params are necessary:

```kotlin
// Explicitly set MATCH_PARENT layout params to force remeasurement when the native
// view is moved between containers of different sizes (e.g., Grid tile → Active Speaker spotlight).
// Without explicit params, the view can retain cached dimensions from the previous container.
```

This will help future maintainers understand the reasoning.
