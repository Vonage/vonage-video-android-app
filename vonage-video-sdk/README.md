# vonage-video-sdk

An Android library that wraps the [OpenTok Android SDK](https://tokbox.com/developer/sdks/android/) behind clean, mockable Kotlin interfaces. Other modules in this project depend only on these abstractions, keeping business logic fully decoupled from the underlying SDK implementation.

---

## Overview

| Concern | Type |
|---|---|
| Session management | `VonageSession` / `VonageSessionListener` |
| Publishing (camera & screen) | `VonagePublisher` / `VonagePublisherConfig` |
| Subscribing to remote streams | `VonageSubscriber` |
| Stream metadata | `VonageStream`, `VonageConnection` |
| Error handling | `VonageError` |
| Object creation | `VonageSdkFactory` |

---

## Key Interfaces

### `VonageSdkFactory`

Entry point for creating SDK objects. Use `VonageSdkFactory.create()` to obtain the default OpenTok-backed implementation, or supply a fake in tests.

```kotlin
val factory: VonageSdkFactory = VonageSdkFactory.create()
```

### `VonageSession`

Manages the lifecycle of a video session.

```kotlin
val session = factory.createSession(context, apiKey, sessionId)
session.setSessionListener(myListener)
session.connect(token)

// Publishing
val publisher = factory.createPublisher(context, VonagePublisherConfig())
session.publish(publisher)

// Subscribing
session.setSessionListener(object : VonageSessionListener {
    override fun onStreamReceived(stream: VonageStream) {
        val subscriber = session.subscribe(context, stream)
    }
    // ...
})
```

### `VonagePublisher`

Controls the local video/audio stream. Supports blur, bitrate, degradation preferences, and noise suppression.

```kotlin
val config = VonagePublisherConfig(
    name = "My Camera",
    blurLevel = VonageBlurLevel.HIGH,
    captureResolution = VonageCaptureResolution.HIGH,
    captureFrameRate = VonageCaptureFrameRate.FPS_30,
)
val publisher = factory.createPublisher(context, config)
publisher.publishVideo = true
publisher.publishAudio = true
publisher.cycleCamera()
```

Screen sharing:

```kotlin
val screenSharePublisher = factory.createScreenSharePublisher(
    context,
    VonageScreenShareConfig(name = "Screen", mediaProjection = projection),
)
```

### `VonageSubscriber`

Controls a remote participant's stream.

```kotlin
subscriber.subscribeToVideo = true
subscriber.setCaptionsListener { name, streamId, text, isFinal -> /* ... */ }
```

---

## Data Models

| Class | Description |
|---|---|
| `VonageStream` | Snapshot of a stream (id, name, video type, audio/video flags) |
| `VonageConnection` | A session connection (id, creation time) |
| `VonageError` | Error with `code`, `message`, and `domain` |

---

## Enums

| Enum | Values |
|---|---|
| `VonageVideoType` | `CAMERA`, `SCREEN`, `CUSTOM` |
| `VonageBlurLevel` | `NONE`, `LOW`, `HIGH` |
| `VonageBitratePreset` | `DEFAULT`, `BW_SAVER`, `EXTRA_BW_SAVER`, `CUSTOM` |
| `VonageDegradationPref` | `NOT_SET`, `MAINTAIN_FRAME_RATE_AND_RESOLUTION`, `MAINTAIN_FRAME_RATE`, `MAINTAIN_RESOLUTION`, `BALANCED` |
| `VonageCaptureResolution` | `LOW`, `MEDIUM`, `HIGH`, `HIGH_1080P` |
| `VonageCaptureFrameRate` | `FPS_1`, `FPS_7`, `FPS_15`, `FPS_30` |
| `VonageVideoCodec` | `VP8`, `H264`, `VP9` |
| `VonageNoiseSuppression` | `ENABLED`, `DISABLED` |

---

## Testing

All public types are interfaces or data classes with no dependency on Android SDK or OpenTok classes, making them straightforward to mock:

```kotlin
val mockSession = mockk<VonageSession>()
val mockFactory = mockk<VonageSdkFactory>()
every { mockFactory.createSession(any(), any(), any()) } returns mockSession
```

---

## Module Dependencies

```
vonage-video-sdk
 ├── vonage-video-shared
 ├── vonage-android-logger
 └── opentok-android-sdk (internal only)
```

The OpenTok dependency is used only inside the `internal` package and is never exposed through the public API.

