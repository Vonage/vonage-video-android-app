# Plan: Fix subscriber video/audio state not updating grid layout and active speaker

## Overview

Four interlocking bugs cause the video grid and active speaker layout to show stale or
incorrect state when a remote participant toggles their camera or microphone.

| # | Bug | Root-cause file(s) |
|---|-----|--------------------|
| A | `_isMicEnabled` is permanently stuck at its subscribe-time value | `OpenTokSession.kt`, `ParticipantState.kt` |
| B | Scroll-based visibility optimisation corrupts `_isCameraEnabled` | `ParticipantState.kt` |
| C | `changeVisibility(true)` re-enables video decoding using a stale snapshot | `ParticipantState.kt` |
| D | A camera-off participant can be promoted to the active-speaker spotlight | `Call.kt` |

---

## Branch name
`feature/fix-subscriber-av-state-updates`
Run: `/worktree feature/fix-subscriber-av-state-updates`

---

## Root-cause analysis

### Bug A – `_isMicEnabled` is never updated

`ParticipantState._isMicEnabled` is initialised from `vonageSubscriber.stream.hasAudio`
(line 56 of `ParticipantState.kt`). The `VonageStream` data class is an **immutable
snapshot** captured once in `OpenTokSubscriber` (line 27: `override val stream: VonageStream
= raw.stream.toVonage()`). No code path ever writes to `_isMicEnabled` again.

The OpenTok SDK fires `Session.StreamPropertiesListener.onStreamPropertyChanged(session,
stream, changedProperty, oldValue, newValue)` whenever a remote publisher toggles their
microphone (`changedProperty == StreamProperties.HAS_AUDIO`) or camera
(`StreamProperties.HAS_VIDEO`). This listener is **never registered** in
`OpenTokSession.setSessionListener()`, which only wires up `Session.SessionListener`
(lines 87–109 of `OpenTokSession.kt`). Consequently every remote mic-toggle event is
silently dropped.

### Bug B – scroll visibility corrupts `_isCameraEnabled`

The bandwidth-optimisation path in `Call.updateParticipantVisibilityFlow()` calls
`participantState.changeVisibility(false)` when a tile scrolls off-screen. This sets
`vonageSubscriber.subscribeToVideo = false`, which causes the OpenTok SDK to fire
`SubscriberKit.VideoListener.onVideoDisabled(reason="subscribe")` back into the app.
`ParticipantState.setup()` handles `onVideoDisabled` unconditionally, writing
`_isCameraEnabled.value = false` even though the remote camera has not changed state.
The symmetrical `onVideoEnabled(reason="subscribe")` fires when the tile scrolls back
and resets `_isCameraEnabled.value = true`, regardless of the publisher's actual state.

The OpenTok `reason` string distinguishes the cause:
- `"publishVideo"` – the remote publisher toggled their camera (should update state)
- `"quality"` – subscriber-side network degradation (should update state)
- `"subscribe"` – `subscribeToVideo` was changed locally (must be **ignored**)

### Bug C – `changeVisibility(true)` uses a stale snapshot

`ParticipantState.changeVisibility(visible = true)` (line 83) executes:

```kotlin
vonageSubscriber.subscribeToVideo = vonageSubscriber.stream.hasVideo
```

`vonageSubscriber.stream` is the immutable snapshot from subscription time
(`OpenTokSubscriber.stream` is a `val`, not a computed property). If the remote camera
was disabled after the subscription, `stream.hasVideo` is still `true`, so the SDK is
instructed to decode a video stream that the publisher is no longer sending. The live
source of truth is `_isCameraEnabled`, which (after Bug B is fixed) correctly tracks the
publisher's camera state.

### Bug D – camera-off participant can win the spotlight

`Call.startActiveSpeakerTracker()` (lines 647–659) promotes any participant to active
speaker purely based on their audio level. There is no guard checking
`mainSpeaker.isCameraEnabled.value`. A participant who has disabled their camera and
starts speaking will be promoted to the large spotlight slot, which then shows their
avatar — wasting the primary real-estate and creating a confusing UX.

---

## Affected files

| File | Change |
|------|--------|
| `vonage-video-sdk/src/main/java/com/vonage/android/kotlin/sdk/VonageSessionContract.kt` | Add `onStreamPropertyChanged` default method to `VonageSessionListener` |
| `vonage-video-sdk/src/main/java/com/vonage/android/kotlin/sdk/internal/OpenTokSession.kt` | Register `Session.StreamPropertiesListener` alongside `Session.SessionListener`; route `HAS_AUDIO`/`HAS_VIDEO` changes to the listener |
| `vonage-video-core/src/main/java/com/vonage/android/kotlin/model/ParticipantState.kt` | Fix `changeVisibility`; filter `"subscribe"` reason in video listener; add `updateStreamProperties()` |
| `vonage-video-core/src/main/java/com/vonage/android/kotlin/Call.kt` | Handle `onStreamPropertyChanged` in session listener; guard active-speaker promotion with camera check |
| `vonage-video-core/src/test/java/com/vonage/android/kotlin/model/ParticipantStateTest.kt` | **New file** – unit tests for all four fixes |
| `vonage-video-core/src/test/java/com/vonage/android/kotlin/CallTest.kt` | New tests for stream-property propagation and camera-gated active-speaker promotion |

---

## Implementation steps

### Step 1 — Extend `VonageSessionListener` with a default `onStreamPropertyChanged`

**File:** `vonage-video-sdk/src/main/java/com/vonage/android/kotlin/sdk/VonageSessionContract.kt`

Add a new default method at the bottom of `VonageSessionListener`. Using a default body
makes this backward-compatible with all existing anonymous-object implementations:

```kotlin
interface VonageSessionListener {
    fun onConnected()
    fun onDisconnected()
    fun onStreamReceived(stream: VonageStream)
    fun onStreamDropped(stream: VonageStream)
    fun onError(error: VonageError)
    /**
     * Called when a remote stream's audio or video property changes.
     * Fires when the remote publisher toggles their microphone or camera.
     *
     * @param streamId  The affected stream ID.
     * @param hasVideo  Current video state of the stream.
     * @param hasAudio  Current audio state of the stream.
     */
    fun onStreamPropertyChanged(streamId: String, hasVideo: Boolean, hasAudio: Boolean) {}
}
```

---

### Step 2 — Wire `Session.StreamPropertiesListener` in `OpenTokSession`

**File:** `vonage-video-sdk/src/main/java/com/vonage/android/kotlin/sdk/internal/OpenTokSession.kt`

The two required imports:
```kotlin
import com.opentok.android.StreamProperties
```

Modify `setSessionListener`:

```kotlin
override fun setSessionListener(listener: VonageSessionListener?) {
    if (listener == null) {
        session.setSessionListener(null)
        session.setStreamPropertiesListener(null)   // ← new: clear companion listener
        return
    }
    session.setSessionListener(object : Session.SessionListener {
        // ... existing onConnected / onDisconnected / onStreamReceived /
        //     onStreamDropped / onError — unchanged
    })
    // ← new block: register stream-properties listener on the same session
    session.setStreamPropertiesListener { _, stream, changedProperty, _, _ ->
        if (changedProperty == StreamProperties.HAS_AUDIO
            || changedProperty == StreamProperties.HAS_VIDEO) {
            listener.onStreamPropertyChanged(
                streamId = stream.streamId,
                hasVideo  = stream.hasVideo(),
                hasAudio  = stream.hasAudio(),
            )
        }
        // VIDEO_DIMENSIONS changes are intentionally ignored here.
    }
}
```

`stream.hasVideo()` / `stream.hasAudio()` on the OpenTok `Stream` object return the
**current** (post-change) values, so both states are always transmitted together. This
avoids partial-update races.

---

### Step 3 — Fix `ParticipantState` (Bugs A, B, C)

**File:** `vonage-video-core/src/main/java/com/vonage/android/kotlin/model/ParticipantState.kt`

#### 3a — Add the `updateStreamProperties` method (Bug A)

Add after the `stream` property declaration (after line 79):

```kotlin
/**
 * Updates camera and microphone state from a session-level stream-property change.
 * Called by [Call] when the remote publisher toggles their camera or microphone.
 */
internal fun updateStreamProperties(hasVideo: Boolean, hasAudio: Boolean) {
    _isCameraEnabled.value = hasVideo
    _isMicEnabled.value    = hasAudio
}
```

#### 3b — Filter the `"subscribe"` reason in `VonageSubscriberVideoListener` (Bug B)

Add a private constant at the bottom of the file (in the `// region Extension helpers`
block is fine, or at companion-object level):

```kotlin
private const val VIDEO_REASON_SUBSCRIBE = "subscribe"
```

In `setup()`, update the video-listener callbacks:

```kotlin
vonageSubscriber.setVideoListener(object : VonageSubscriberVideoListener {
    override fun onVideoEnabled(reason: String) {
        vonageLogger.d(logTag, "Subscriber video enabled, reason=$reason")
        if (reason != VIDEO_REASON_SUBSCRIBE) {        // ← guard added
            _isCameraEnabled.value = true
        }
    }

    override fun onVideoDisabled(reason: String) {
        vonageLogger.d(logTag, "Subscriber video disabled, reason=$reason")
        if (reason != VIDEO_REASON_SUBSCRIBE) {        // ← guard added
            _isCameraEnabled.value = false
        }
    }
    // onVideoDataReceived / onVideoDisableWarning / onVideoDisableWarningLifted unchanged
})
```

#### 3c — Fix `changeVisibility` to use live state instead of stale snapshot (Bug C)

Replace line 83:

```kotlin
// Before
true -> vonageSubscriber.subscribeToVideo = vonageSubscriber.stream.hasVideo

// After
true -> vonageSubscriber.subscribeToVideo = _isCameraEnabled.value
```

The full method after the fix:

```kotlin
override fun changeVisibility(visible: Boolean) {
    when (visible) {
        true  -> vonageSubscriber.subscribeToVideo = _isCameraEnabled.value
        false -> vonageSubscriber.subscribeToVideo = false
    }
}
```

---

### Step 4 — Handle `onStreamPropertyChanged` and guard active-speaker in `Call`

**File:** `vonage-video-core/src/main/java/com/vonage/android/kotlin/Call.kt`

#### 4a — Dispatch stream-property changes to `ParticipantState` (Bug A)

Inside the `VonageSessionListener` anonymous object in `connect()` (after the existing
`onError` override), add:

```kotlin
override fun onStreamPropertyChanged(
    streamId: String,
    hasVideo: Boolean,
    hasAudio: Boolean,
) {
    (participants[streamId] as? ParticipantState)
        ?.updateStreamProperties(hasVideo, hasAudio)
}
```

This is a no-op if the stream is not yet in `participants` (race between subscribe and
property change), which is safe.

#### 4b — Gate active-speaker promotion on camera state (Bug D)

Replace the body of `startActiveSpeakerTracker()` (lines 647–659):

```kotlin
private fun startActiveSpeakerTracker() {
    activeSpeakerTrackerJob?.cancel()
    activeSpeakerTrackerJob = activeSpeakerTracker.activeSpeakerChanges
        .onEach { payload ->
            participants[payload.newActiveSpeaker.streamId]?.let { mainSpeaker ->
                val screenSharingParticipant = participants.values.firstScreenSharing()
                when {
                    // Screen share always wins — camera state is irrelevant.
                    screenSharingParticipant != null -> {
                        _activeSpeaker.update { screenSharingParticipant }
                    }
                    // Only promote a camera participant if their camera is on.
                    mainSpeaker.isCameraEnabled.value -> {
                        mainSpeaker.changeVisibility(true)
                        _activeSpeaker.update { mainSpeaker }
                    }
                    // Camera is off: ignore this promotion; the spotlight keeps its
                    // current occupant (or shows a spacer if there is none).
                }
            }
        }
        .launchIn(coroutineScope)
}
```

**Why `updateParticipants()` is unchanged:** it only fires on add/remove, not on
property changes. The current active speaker is preserved across participant-list updates
(line 676), which is correct — we don't demote someone mid-conversation if they briefly
toggle their camera.

---

### Step 5 — Write `ParticipantStateTest` (new file)

**File:** `vonage-video-core/src/test/java/com/vonage/android/kotlin/model/ParticipantStateTest.kt`

Follow the same `mockk` + `kotlinx-coroutines-test` pattern used in `CallTest.kt`. The
test needs a `VonageSubscriber` mock and needs to call `setup()` on the
`ParticipantState` to register listeners, then simulate callbacks by capturing the
registered `VonageSubscriberVideoListener`.

Tests to write (use `given_<precondition>_THEN_<outcome>` naming from AGENTS.md):

| Test | Scenario |
|------|----------|
| `given_videoDisabledWithReasonSubscribe_THEN_isCameraEnabledUnchanged` | `onVideoDisabled("subscribe")` → `_isCameraEnabled` stays `true` |
| `given_videoDisabledWithReasonPublishVideo_THEN_isCameraEnabledFalse` | `onVideoDisabled("publishVideo")` → `_isCameraEnabled = false` |
| `given_videoEnabledWithReasonSubscribe_THEN_isCameraEnabledUnchanged` | Start with camera off; `onVideoEnabled("subscribe")` → stays `false` |
| `given_videoEnabledWithReasonPublishVideo_THEN_isCameraEnabledTrue` | `onVideoEnabled("publishVideo")` → `_isCameraEnabled = true` |
| `given_updateStreamProperties_THEN_isCameraAndMicUpdated` | `updateStreamProperties(hasVideo=false, hasAudio=false)` → both flows update |
| `given_changeVisibilityTrueWithCameraOff_THEN_subscribeToVideoFalse` | `_isCameraEnabled = false`, `changeVisibility(true)` → `subscribeToVideo = false` |
| `given_changeVisibilityTrueWithCameraOn_THEN_subscribeToVideoTrue` | `_isCameraEnabled = true`, `changeVisibility(true)` → `subscribeToVideo = true` |
| `given_changeVisibilityFalse_THEN_subscribeToVideoFalse` | `changeVisibility(false)` → `subscribeToVideo = false` regardless of camera state |

---

### Step 6 — Extend `CallTest`

**File:** `vonage-video-core/src/test/java/com/vonage/android/kotlin/CallTest.kt`

Add new tests in existing sections or new `// region Stream properties` and
`// region Active speaker promotion` sections:

| Test | Scenario |
|------|----------|
| `onStreamPropertyChanged with known streamId should update participant mic state` | Fire `onStreamPropertyChanged(streamId, hasVideo=true, hasAudio=false)` after subscribe; verify `isMicEnabled.value == false` on the `ParticipantState` |
| `onStreamPropertyChanged with unknown streamId should be a no-op` | Fire with a random ID; no exception, participants unchanged |
| `activeSpeaker should not be promoted when camera is off` | Set up subscriber with `isCameraEnabled = false`; emit `activeSpeakerChanges`; assert `activeSpeaker.value` is NOT updated to that participant |
| `activeSpeaker should be promoted when camera is on` | Set up subscriber with `isCameraEnabled = true`; emit `activeSpeakerChanges`; assert `activeSpeaker.value == mainSpeaker` |
| `screenSharing participant should always win active speaker regardless of camera state` | Screen-share participant + camera-off condition; assert screen-share wins |

> **Note on testability:** `CallTest` currently captures `capturedSessionListener` from
> `mockSession.setSessionListener(any())`. The new `onStreamPropertyChanged` override is
> reachable via `capturedSessionListener!!.onStreamPropertyChanged(...)`. No new test
> infrastructure is required.
>
> For `activeSpeaker` promotion tests, `ActiveSpeakerTracker` is constructed internally
> inside `Call`. The test must drive `isCameraEnabled` on the subscriber mock (the mock
> already uses `relaxed = true`; add `every { isCameraEnabled } returns
> MutableStateFlow(false)` as needed). The `activeSpeakerChanges` shared flow cannot be
> triggered from outside `Call`; the test should drive it indirectly via audio-level
> updates. Alternatively, extract `ActiveSpeakerTracker` as an injectable dependency of
> `Call` (constructor parameter already supports this pattern for `PublisherFactory`); this
> would allow injecting a mock tracker in tests.

---

## Edge cases & risks

| Risk | Mitigation |
|------|------------|
| **Double-write to `_isCameraEnabled`:** both `onStreamPropertyChanged` (via `updateStreamProperties`) and `onVideoDisabled/Enabled(reason="publishVideo")` fire when the remote publisher toggles camera. Both paths write the same value, so there is no conflict — the writes are idempotent. | No action needed; the redundancy is harmless. |
| **Race between `onStreamReceived` and `onStreamPropertyChanged`:** if a property-change fires before `addSubscriber` completes (unlikely but possible), `participants[streamId]` will be null and `updateStreamProperties` is a no-op. The subscriber will shortly be added with its initial stream snapshot (`hasVideo`, `hasAudio`), which may already be stale. | Acceptable risk for now. A future improvement could queue property-change events and replay them after `addSubscriber` completes. |
| **Camera-off active speaker demotion gap:** if the *current* active speaker disables their camera mid-conversation, they stay in the spotlight (showing avatar) because `_activeSpeaker` only updates on `activeSpeakerChanges` events, not on `isCameraEnabled` changes. Step 4b only gates *new promotions*. | Acceptable. Adding reactive demotion would require collecting `isCameraEnabled` changes inside `startActiveSpeakerTracker` — a larger, separate change. Document this in a follow-up. |
| **`StreamProperties` import:** the OpenTok `StreamProperties` class must be imported in `OpenTokSession.kt`. Confirm the exact package; it is typically `com.opentok.android.StreamProperties`. | Build will fail immediately if wrong; easy to fix. |
| **`Session.StreamPropertiesListener` lambda syntax:** the OpenTok SDK uses a Java interface. Verify SAM-conversion is available (it is, since the interface has a single abstract method). | If not, use an anonymous object instead. |
| **`VonageSessionListener` is a Kotlin `interface` with a new default method:** all existing anonymous-object implementations in `Call.kt` and tests will compile without change. The `binary-compatibility-validator` is not applied to `vonage-video-sdk` or `vonage-video-core` (only to `vonage-audio-selector` and `vonage-android-logger`), so no `.api` dump update is needed. | Confirmed safe. |
| **Snapshot tests:** no UI components in `vonage-video-ui-compose` are changed, so Roborazzi goldens do not need to be updated. | Verify with `./gradlew :vonage-video-ui-compose:verifyRoborazziDebug`. |

---

## Testing

```bash
# Unit tests for affected modules
./gradlew :vonage-video-core:test
./gradlew :vonage-video-sdk:test    # sanity check — no logic changes here

# Full quality gate (lint + coverage + detekt)
./gradlew clean koverXmlReportDebug detekt

# Manual verification steps
# 1. Join a call as Participant A on device A, Participant B on device B.
# 2. B mutes their mic → verify mic indicator on A's screen updates immediately.
# 3. B unmutes → verify indicator restores.
# 4. B disables camera → verify avatar appears on A's screen; B is NOT shown in spotlight.
# 5. B re-enables camera → verify video feed restores.
# 6. Scroll B's tile off-screen → scroll back → verify camera state is preserved (no
#    momentary avatar flash).
# 7. B disables camera and talks loudly → verify B is NOT promoted to spotlight;
#    prior active speaker or empty spacer shown instead.
# 8. A shares screen → verify screen share still overrides active speaker regardless
#    of camera state.
```
