# Adaptive Video Quality Policy

The app adjusts video quality to how large the video is actually drawn on screen. A thumbnail
does not need 720p at 30 FPS, and decoding or sending it wastes bandwidth, CPU and battery.

The policy has two halves:

| Side | Decides from | Changes | Code |
|---|---|---|---|
| Receive (subscriber) | Rendered size of each remote video | Simulcast layer and frame rate the media router forwards | `SubscriberVideoQualityPolicy`, `ParticipantState` |
| Send (publisher) | Number of remote camera streams, local speaking state | Publisher video bitrate preset | `PublisherVideoQualityPolicy`, `Call.startPublisherQualityPolicy` |

Both live in `vonage-video-core` (`internal/VideoQualityPolicy.kt`). The policy is always on.
There is no feature flag.

## Requirements and limits

- **Routed sessions only.** Preferred resolution and frame rate are hints to the media router.
  In relayed (peer-to-peer) sessions the SDK ignores them, and the receive side has no effect.
- **Simulcast publishers.** The router can only forward layers the publisher sends. A 720p
  camera publisher sends 1280x720, 640x360 and 320x180 layers. A publisher that sends a single
  layer is received as-is, whatever tier is requested.
- The preference is an upper bound. The router can still send a lower layer when the network
  is congested.

## Receive side: subscriber tiers

`ParticipantVideoRenderer` (`vonage-video-ui-compose`) reports its size in px through
`Modifier.onSizeChanged` → `Participant.onRenderedSizeChanged(width, height)`. Window resizes,
rotation, layout switches (grid ↔ speaker), pinning and participants joining or leaving all
change tile sizes, so all of them reach the policy without extra wiring.

The renderer letterboxes (`STYLE_VIDEO_FIT`), so the policy first fits the source aspect ratio
into the view. The source aspect comes from the latest subscriber video stats. Until stats
arrive, it assumes 16:9 landscape. The **short edge of the fitted video** picks the tier:

| Tier | Fitted short edge | Preferred resolution | Preferred FPS |
|---|---|---|---|
| `THUMBNAIL` | ≤ 240 px | 320x180 (180x320 for portrait sources) | 15 |
| `MEDIUM` | ≤ 540 px | 640x360 (360x640 for portrait sources) | 30 |
| `FULL` | > 540 px | no preference (top layer) | no preference |

Why these numbers:

- Each tier maps to a simulcast layer, so the router never has to guess.
- A layer is used up to about 1.33x (180 → 240) or 1.5x (360 → 540) upscaling. Beyond that,
  softness becomes visible on phone screens, so the next layer up is chosen.
- 15 FPS is enough for a face in a thumbnail. Medium and full keep the publisher's frame rate.

Rules on top of the table:

- **Screen shares are always `FULL`.** They usually carry text, which does not survive downscaling.
- **Upgrades are immediate, downgrades need a 15% margin.** Growing past a threshold upgrades
  right away. Shrinking only downgrades once the size is 15% below the threshold (204 px and
  459 px). Sizes that hover around a threshold therefore stay at the higher quality.
- **Changes settle for 300 ms.** Only the size that stays stable for 300 ms is applied, so
  animations and multi-pass layouts do not cause a burst of layer switches.
- **No size, no change.** Until a tile is first laid out, and for zero sizes, the subscriber
  keeps its previous preference. New subscribers start at `FULL`.
- Camera-off participants show an avatar instead of the renderer, so no size is reported.

Each tier change is logged at debug level: `Subscriber[<streamId>] Video tier=THUMBNAIL rendered=320x180`.

## Send side: publisher bitrate

A publisher cannot know how large each remote client draws it. The number of remote camera
streams (screen shares excluded) is used as a proxy, because the more people are in the call,
the smaller every tile is on everyone's screen.

| Remote camera streams | Preset applied |
|---|---|
| 0–2 | user configuration (unchanged) |
| 3–6 | `BW_SAVER` |
| 7+ | `EXTRA_BW_SAVER` |

Rules on top of the table:

- **The user's explicit choice wins.** If the user picked any preset other than `DEFAULT` in
  Settings (including `CUSTOM`), the policy never touches it.
- **Speaking restores full quality.** Other clients may put the active speaker in the
  spotlight, so while the local user is talking, the user's configuration is applied. It is
  kept for 5 s after they stop talking, so pauses between sentences do not flip the bitrate.
- Bitrate presets are applied at runtime without restarting the encoder. Capture resolution and
  frame rate are not changed during a call, because that would require republishing. The user
  can still pick them in Settings.
- The screen-share publisher is not affected.

## Tests

- `VideoQualityPolicyTest`: thresholds, margin, letterboxing, portrait sources, screen share,
  publisher room sizes, speaking hold.
- `ParticipantStateVideoQualityTest`: rendered size → `setPreferredResolution` /
  `setPreferredFrameRate`, settle time, screen share.
- `CallPublisherQualityTest`: room growth, speaking, user-selected bitrate in a real `Call`.

## Measuring impact

Use a routed session, a debug build and the same devices for every run. Compare each scenario
with the policy on (this branch) and off (`develop`). Run each scenario for 10 minutes.

### Scenarios

| # | Layout | Participants | What it checks |
|---|---|---|---|
| 1 | 1:1, remote full screen | 2 | No regression in full-screen quality |
| 2 | Speaker layout | 5 | Speaker full, filmstrip thumbnails |
| 3 | Grid | 5 | All medium/thumbnail |
| 4 | Grid | 9+ | All thumbnails, publisher on `EXTRA_BW_SAVER` |
| 5 | Grid → speaker → grid, rotate | 5 | Dynamic switching, no flapping |

### Metrics

- **Receive bandwidth and resolution.** Subscriber video stats (`ParticipantState.videoStats`:
  `bitrate`, `width`, `height`, `decodedFrameRate`) are sampled by the SDK. Filter logcat for
  the tier log line and the stats, for example:
  `adb logcat | grep -E "Video tier=|Subscriber\["`.
- **Send bandwidth.** Publisher layer stats (`PublisherState.videoStats.videoLayerStats`:
  `bitrate` per layer).
- **CPU.** Android Studio Profiler → CPU, average app CPU % over the run. Or
  `adb shell top -b -d 5 -n 120 | grep com.vonage.android.debug`.
- **Battery.** `adb shell dumpsys batterystats --reset` before the run, unplug the device
  (`adb shell dumpsys battery unplug` over Wi-Fi adb), then
  `adb shell dumpsys batterystats com.vonage.android.debug` after it. Compare the estimated
  power use (mAh).
- **Full-screen quality (scenario 1).** Received resolution and FPS must match `develop`, and
  freeze count must not increase.

### Results

Fill in after device runs. Device: _____, Android _____, SDK 2.36.0.

| # | Receive kbps (off → on) | Send kbps (off → on) | CPU % (off → on) | Battery mAh (off → on) | Notes |
|---|---|---|---|---|---|
| 1 | | | | | |
| 2 | | | | | |
| 3 | | | | | |
| 4 | | | | | |
| 5 | | | | | |
