# Screen sharing validation (VIDSOL-1745)

Manual validation of the Android screen-sharing flow: MediaProjection consent, start/stop, and recovery after interruptions. Pause is out of scope; the app has no pause state (`ScreenSharingState` is `IDLE`, `STARTING`, `SHARING`, `STOPPING`).

## Setup

| Device | Android | Role |
|---|---|---|
| Emulator `Large_Desktop` | 14 (API 34) | Sharer, baseline build (`develop` @ `08b4b19b`) |
| Emulator `Pixel_5_API_34` | 14 (API 34) | Sharer, baseline and fixed build |
| Emulator `Pixel_5_API_30` | 11 (API 30) | Remote viewer (checks the `<name>'s screen` tile appears and goes away) |
| Pixel 9a | 17 (API 37) | Sharer, fixed build plus the follow-up changes below (cases 12–15) |

Debug build, `targetSdk 37`. Each case checks `logcat` for `FATAL EXCEPTION` / `ANR in`, `dumpsys activity services` for `ScreenSharingService`, and the bottom-bar label.

## Results

| # | Case | Baseline | Fixed build |
|---|---|---|---|
| 1 | Consent dialog: Cancel | Pass. No service, button stays "Share screen" | Pass |
| 2 | Consent: Start now, then Stop from the bottom bar | Pass. Viewer tile appears and is removed | Pass |
| 3 | Start again after stopping | Pass | Pass |
| 4 | App backgrounded (Home) while sharing | Pass. Share keeps running, viewer keeps the tile | Pass |
| 5 | Incoming GSM call (ring, accept, hang up) while sharing | Pass | Pass |
| 6 | Screen locked while sharing | Pass. Android 14 keeps projecting | Pass |
| 7 | End call while sharing | Pass. Service stopped, goodbye screen | Pass |
| 8 | App process killed while sharing (`run-as … kill -9`, same as low-memory kill or Task Manager stop) | **Fail. Crash loop**: system restarts the `START_STICKY` service, `startForeground(MEDIA_PROJECTION)` throws `SecurityException` without a consent token, repeated every restart | Pass. Service is not restarted, no crash |
| 9 | Camera permission revoked in Settings while sharing (kills the process) | **Fail.** Same crash loop as #8 | Pass |
| 10 | Stop the share before it is published (end call / stop during `STARTING`) | **Fail by code inspection**: `Call.stopCapturingScreen()` casts a missing participant (`null as PublisherState`) and throws. Not reproducible reliably by hand, timing window is a few ms | Pass. Covered by `CallTest` |
| 11 | `getMediaProjection` returns null or throws (stale or reused token) | **Fail by code inspection**: button stuck in `STARTING`, tap ignored, foreground service left running | Pass. Covered by `EnabledScreenSharingTest` |
| 12 | Pixel 9a, "Share entire screen", then Stop from the status-bar chip dialog | Not run | Pass. Services and notification removed, button back to "Share screen", screen tile removed |
| 13 | Pixel 9a, "Share one app" (Calculator), system switches to that app, then Stop from the chip while in it | Not run | Pass. Same cleanup as #12 when returning to the call |
| 14 | Pixel 9a, foreground-service notification while sharing | Generic "app is running" entry (emulator run) | Pass. Title, text and screen-share icon shown |
| 15 | Pixel 9a, StrictMode while the border overlay starts (overlay allowed with `appops set … SYSTEM_ALERT_WINDOW allow`) | Violation logged on the emulator run (not re-run on the Pixel 9a) | Pass. No StrictMode violation logged |

One ANR was seen on the baseline run (#5). Its trace shows the main thread waiting on the render thread while `system_server` itself was blocked for 65 s and then killed by the watchdog: the host was running three emulators. It did not reproduce on a fresh emulator with the baseline or fixed build, so it is not attributed to the app.

## Fixes in this branch

- `Call.startCapturingScreen` / `stopCapturingScreen` (`vonage-video-core`): publish and register the screen publisher in one main-thread block, keep the job, and make stop cancel a pending start and no-op when nothing is shared.
- `EnabledScreenSharing.onServiceConnected`: a null or throwing `getMediaProjection` now stops sharing and calls `onStopped`, so the UI returns to `IDLE`.
- `ScreenSharingService`: `START_NOT_STICKY`, and `startForeground` failures stop the service instead of crashing. A media projection can't be resumed without new user consent, so restarting the service has no value.
- `ScreenSharingService` notification: localized title and text, a monochrome small icon, and a localized channel name (was the hardcoded `Screen_Capture`). Strings in `vonage-feature-screensharing/src/enabled/res/values/strings.xml`.
- `ScreenSharingOverlayService`: on API 30+ the overlay view and `WindowManager` come from a window context (`createWindowContext(TYPE_APPLICATION_OVERLAY)`) instead of the Service context.
- `VeraPublisherHolder.screenPublisher` removed. Nothing read it, and it kept the last screen publisher (and its `MediaProjection`) alive after stop. `Call.participants` already owns the screen publisher.

## Not fixed here (follow-ups)

- The green border overlay only shows if "Display over other apps" was granted elsewhere; the app never asks for it. Needs a product decision: request it, or drop the overlay and `SYSTEM_ALERT_WINDOW` and rely on the system indicators (status-bar chip, confirmed on Android 17).
