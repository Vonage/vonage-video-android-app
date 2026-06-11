# Architecture

## Project Architecture

This reference app requires a deployed backend. You can find backend code and deployment instructions in the [vonage-video-react-app](https://github.com/Vonage/vonage-video-react-app) repository.

The backend communicates with the Vonage video platform using the Vonage Server SDK and is responsible for generating the session IDs and tokens used to connect to video rooms by the Vonage Client SDK.

### Session bootstrap path

```
APIService.getSession()
  → SessionRepository
  → MeetingRoomScreenViewModel.connect(...)
  → VonageVideoClient.initializeSession(...)
```

`BuildConfig.BASE_API_URL` propagates to three locations: the Retrofit base URL (`RetrofitModule.kt`), deep links (`AppNavHost.kt`), and sharing links (`util/navigateToShare.kt`).

### Dependency injection

Hilt DI is used throughout `app/`. The `vonage-meeting-room` module uses a **manual** `MeetingRoomContainer` — Hilt must not be introduced into that module. Feature signal plugins (chat, reactions) are injected into `VonageVideoClient` via `SdkModule.provideVonageVideoClient(...)`.

## Module Overview

The app is organized into the following modules:

| Module | Role |
|---|---|
| `app/` | Composition root: navigation, Hilt DI, networking, screen orchestration |
| `vonage-meeting-room/` | Self-contained prebuilt meeting-room library — no Hilt, manual DI via `MeetingRoomContainer`. Public API is only the classes in `api/`; everything else is `internal`. |
| `vonage-meeting-room-sample-app/` | Minimal host app demonstrating `vonage-meeting-room` in isolation |
| `vonage-video-core/` | Core video SDK wrapper: `VonageVideoClient`, `Call` abstraction, signaling, and domain models on top of the OpenTok Android SDK |
| `vonage-video-sdk/` | Mockable Kotlin interfaces over the OpenTok SDK (`VonageSession`, `VonageSdkFactory`, etc.). Other modules depend on these — never on OpenTok directly. |
| `vonage-video-ui-compose/` | Jetpack Compose UI component library with a JSON-driven theme generator, reusable widgets, and permission handling |
| `vonage-video-shared/` | Shared utilities and common code used across all other modules |
| `vonage-feature-chat/` | In-call text chat using OpenTok signaling (optional feature module) |
| `vonage-feature-reactions/` | In-call emoji reactions (optional feature module) |
| `vonage-feature-captions/` | Live captions and subtitles (optional feature module) |
| `vonage-feature-archiving/` | Call recording and archive management (optional feature module) |
| `vonage-feature-screensharing/` | Screen sharing via MediaProjection and foreground service (optional feature module) |
| `vonage-feature-video-effects/` | Video effects such as background blur and replacement (optional feature module) |
| `vonage-feature-audio-effects/` | Audio effects processing (optional feature module) |
| `vonage-feature-settings/` | In-call settings panel (optional feature module) |
| `vonage-audio-selector/` | Audio output device selector (Bluetooth, wired headset, earpiece, speaker) |
| `vonage-android-logger/` | Lightweight logging library with an interceptor pipeline and structured log events |
| `vonage-config-idea-plugin/` | Android Studio / IntelliJ plugin for managing configurable features |
| `build-tools/` | Custom Gradle plugins: `com.vonage.json-config`, `com.vonage.theme-generator`, Detekt, Kover, and SonarQube integration |

## Feature Toggle Pattern

Optional modules use flavor dimensions with `enabled`/`disabled` product flavors:

- Public contracts live in `src/main`; behavior implementations live in `src/enabled` and `src/disabled`.
- When changing a feature API, update **both** flavor source sets.
- `vonage-meeting-room` accepts a runtime filter: `MeetingRoomBuilder.enabledFeatures(Set<MeetingRoomFeature>)`. A feature is active only when its **compile-time flavor is `enabled`** AND it is **present in the runtime set**.

See [CONFIGURATION.md](CONFIGURATION.md) for how to enable/disable features via `config/app-config.json`.

## Active Refactoring

`vonage-meeting-room` is under active refactoring: use cases are being introduced and `Call.kt` (~800 lines) is being split into focused collaborators. Consult this document and the inline comments in that module before making changes there.
