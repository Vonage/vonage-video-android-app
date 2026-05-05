# vonage-meeting-room

A self-contained Android library module that delivers a full Vonage Video meeting room experience. It has no dependency on Hilt or any other DI framework and can be dropped into any app with a few lines of code.

---

## Quick Start

```kotlin
val prebuilt = MeetingRoomBuilder(
    baseUrl  = "https://my-backend.example.com",
    roomName = "my-room",
)
    .enabledFeatures(setOf(MeetingRoomFeature.CHAT, MeetingRoomFeature.CAPTIONS))
    .onAction { action ->
        when (action) {
            is MeetingRoomSDKAction.CallDidEnd        -> // navigate to goodbye screen
            is MeetingRoomSDKAction.GoBack            -> // navigate back (user left the call)
            is MeetingRoomSDKAction.ShareRoom         -> shareLink(action.roomName)
            is MeetingRoomSDKAction.NavigateToSettings -> // navigate to settings screen
        }
    }
    .publisherSettings(PublisherSettings(username = "Alice"))
    .build()

// Option A — Activity (simplest)
prebuilt.launch(context)

// Option B — Composable (embedded)
setContent { prebuilt.content() }

// Observe call state from outside
val state by prebuilt.stateHolder.callState.collectAsStateWithLifecycle()
```

---

## API Reference

### `MeetingRoomBuilder`

The main entry point. All methods return `this` for fluent chaining.

| Method | Description |
|--------|-------------|
| `MeetingRoomBuilder(baseUrl, roomName)` | Required. Sets the backend URL and room name. |
| `.enabledFeatures(Set<MeetingRoomFeature>)` | Runtime feature filter (see below). Defaults to all features. |
| `.onAction((MeetingRoomSDKAction) -> Unit)` | Navigation/action callback. |
| `.configuration(MeetingRoomConfiguration)` | UI controls (camera, mic, participant list). |
| `.publisherSettings(PublisherSettings)` | Initial publisher config (username, audio/video flags). |
| `.theme(MeetingRoomTheme)` | Custom color theme. Defaults to `MeetingRoomTheme.vonage`. |
| `.isDebug(Boolean)` | Enables verbose HTTP logging. |
| `.reportingContent(@Composable (() -> Unit) -> Unit)` | Custom report-issue bottom sheet content. |
| `.build()` | Builds and returns `MeetingRoomPrebuilt`. |

### `MeetingRoomPrebuilt`

The result of `.build()`.

```kotlin
val prebuilt: MeetingRoomPrebuilt = MeetingRoomBuilder(...).build()

prebuilt.launch(context)          // Option A — Activity
prebuilt.content()                // Option B — @Composable
prebuilt.stateHolder.callState    // StateFlow<MeetingRoomCallState>
```

### `MeetingRoomSDKAction`

```kotlin
sealed class MeetingRoomSDKAction {
    data object CallDidEnd : MeetingRoomSDKAction()           // call ended
    data class GoBack(val roomName: String)                   // user left the call
    data class ShareRoom(val roomName: String)                // share room link
    data object NavigateToSettings : MeetingRoomSDKAction()  // open settings
}
```

### `MeetingRoomFeature`

```kotlin
enum class MeetingRoomFeature {
    CHAT, ARCHIVING, CAPTIONS, REACTIONS,
    SETTINGS, SCREEN_SHARE, BACKGROUND_EFFECTS, AUDIO_EFFECTS;

    companion object {
        val all: Set<MeetingRoomFeature> = entries.toSet()
    }
}
```

Pass any subset to `.enabledFeatures(...)`. This is a **runtime** filter layered on top of the compile-time Gradle flavors: a feature is active only when its flavor is `enabled` **and** it is present in the set.

### `MeetingRoomConfiguration`

```kotlin
MeetingRoomConfiguration(
    allowCameraControl       = true,  // show camera toggle
    allowMicrophoneControl   = true,  // show mic toggle
    allowShowParticipantList = true,  // show participant list
)
```

### `PublisherSettings`

```kotlin
PublisherSettings(
    username     = "Alice",  // display name
    publishAudio = true,     // start with mic on
    publishVideo = true,     // start with camera on
)
```

### `MeetingRoomTheme`

```kotlin
// Start from defaults and override individual colors
MeetingRoomTheme(
    lightColors = MeetingRoomTheme.vonage.lightColors.copy(primary = Color.Blue),
    darkColors  = MeetingRoomTheme.vonage.darkColors.copy(primary  = Color.Blue),
)
```

### `MeetingRoomStateHolder` / `MeetingRoomCallState`

```kotlin
data class MeetingRoomCallState(
    val isConnected: Boolean,
    val participantCount: Int,
    val isLocalMicEnabled: Boolean,
    val isLocalCameraEnabled: Boolean,
    val roomName: String,
)
```

---

## Feature Flavors

The module preserves the compile-time Gradle flavor system for features that have
enabled/disabled variants. The `MeetingRoomFeature` runtime set acts as an additional filter
on top; it cannot un-disable a compile-time disabled feature.

| Dimension      | Enabled flavor         | Disabled flavor         |
|----------------|------------------------|-------------------------|
| `archiving`    | `archivingEnabled`     | `archivingDisabled`     |
| `captions`     | `captionsEnabled`      | `captionsDisabled`      |
| `screensharing`| `screensharingEnabled` | `screensharingDisabled` |
| `reporting`    | `reportingEnabled`     | `reportingDisabled`     |

The remaining features (chat, reactions, videofx, audiofx, settings) are always included
at the module level; their runtime availability is controlled solely by `MeetingRoomFeature`.

---

## Requirements

- Android minSdk 24
- Kotlin + Java 17
- Jetpack Compose (BOM version declared in the root `libs.versions.toml`)

---

## Architecture

```
vonage-meeting-room/
├── api/                  ← Public surface
│   ├── MeetingRoomBuilder.kt
│   ├── MeetingRoomPrebuilt.kt
│   ├── MeetingRoomFeature.kt
│   ├── MeetingRoomSDKAction.kt
│   ├── MeetingRoomConfiguration.kt
│   ├── PublisherSettings.kt
│   ├── MeetingRoomTheme.kt
│   └── MeetingRoomStateHolder.kt
└── internal/
    ├── MeetingRoomActivity.kt    ← Activity entry point (launched via MeetingRoomPrebuilt)
    ├── MeetingRoomContent.kt     ← Composable entry point (embedded via prebuilt.content())
    ├── MeetingRoomPrebuiltHolder.kt ← Temporary holder for Activity launch
    ├── container/                ← Manual DI (MeetingRoomContainer — lazy factory, no Hilt)
    ├── data/                     ← Networking (OkHttp + Retrofit, scoped to ViewModel lifetime)
    ├── viewmodel/                ← MeetingRoomViewModel + factory
    ├── screen/                   ← All Composable UI (internal, not part of public API)
    ├── service/                  ← Foreground service for ongoing call notification
    └── util/                     ← PiP helpers, noise suppression, context holder
```

All `internal` code is hidden from consumers. The only stable contract is the classes in `api/`.
