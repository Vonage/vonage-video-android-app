# vonage-meeting-room

A self-contained Android library module that delivers a full Vonage Video meeting room experience. It has no dependency on Hilt or any other DI framework and can be dropped into any app with a few lines of code.

---

## Public API

### `MeetingRoomConfig`

Configuration passed to either entry point.

```kotlin
MeetingRoomConfig(
    baseUrl  = "https://my-backend.example.com", // required
    roomName = "my-room",                         // required
    userName = "Alice",                           // optional, defaults to ""
    allowCameraControl       = true,              // show camera toggle
    allowMicrophoneControl   = true,              // show mic toggle
    allowShowParticipantList = true,              // show participant list
)
```

---

## Entry points

### Option A — Activity (simplest)

Launches the meeting room in a new, standalone `Activity`. No Compose setup required on the caller side.

```kotlin
MeetingRoom.launch(
    context = context,
    config  = MeetingRoomConfig(baseUrl = "https://...", roomName = "my-room"),
)
```

The `Activity` handles the back/end-call navigation itself and finishes when the call ends.

### Option B — Composable (embedded)

Embed the meeting room inside your own Compose navigation graph. You control navigation callbacks.

```kotlin
MeetingRoomComponent(
    config              = MeetingRoomConfig(baseUrl = "https://...", roomName = "my-room"),
    onCallEnd           = { navController.popBackStack() },
    onNavigateToSettings = { navController.navigate("settings") }, // optional
    onShare             = { roomName -> shareLink(roomName) },      // optional
)
```

---

## Feature flavors

The module mirrors the same flavor dimensions used by the individual feature modules. Enable or disable each optional capability by selecting the matching flavor combination at build time.

| Dimension      | Enabled flavor       | Disabled flavor       |
|----------------|----------------------|-----------------------|
| chat           | `chatEnabled`        | `chatDisabled`        |
| reactions      | `reactionsEnabled`   | `reactionsDisabled`   |
| archiving      | `archivingEnabled`   | `archivingDisabled`   |
| captions       | `captionsEnabled`    | `captionsDisabled`    |
| screensharing  | `screensharingEnabled` | `screensharingDisabled` |
| videofx        | `videofxEnabled`     | `videofxDisabled`     |
| audiofx        | `audiofxEnabled`     | `audiofxDisabled`     |
| settings       | `settingsEnabled`    | `settingsDisabled`    |

When consuming this module from `app`, add `missingDimensionStrategy` calls to `app/build.gradle.kts` for each dimension, supplying both the `vonage-meeting-room` flavor name and the feature module fallback:

```kotlin
// app/build.gradle.kts — inside defaultConfig { }
missingDimensionStrategy("chat",          "chatEnabled",          "enabled")
missingDimensionStrategy("archiving",     "archivingEnabled",     "enabled")
// …one per dimension
```

---

## Requirements

- Android minSdk 24
- Kotlin + Java 17
- Jetpack Compose (BOM version declared in the root `libs.versions.toml`)

---

## Architecture

```
vonage-meeting-room/
├── api/                  ← Public surface (MeetingRoomConfig, MeetingRoomComponent, MeetingRoomActivity)
└── internal/
    ├── container/        ← Manual DI (MeetingRoomContainer — lazy factory, no Hilt)
    ├── data/             ← Networking (OkHttp + Retrofit, scoped to ViewModel lifetime)
    ├── viewmodel/        ← MeetingRoomViewModel + factory
    ├── screen/           ← All Composable UI (internal, not part of public API)
    ├── service/          ← Foreground service for ongoing call notification
    └── util/             ← PiP helpers, noise suppression, context holder
```

All `internal` code is hidden from consumers. The only stable contract is the three classes in `api/`.
