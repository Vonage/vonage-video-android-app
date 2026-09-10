# Configuration

## Feature configuration

You can fork the repository and start modifying it for your needs, or configure features via the JSON files in the `config/` folder without touching source code.

### `config/app-config.json`

Controls feature flags and application settings:

- Enable or disable features such as chat, captions, screen sharing, background blur, reactions, archiving, and more.
- The custom Gradle plugin (`JsonConfigPlugin`) reads this file at build time and generates both:
  - **`AppConfig.kt`** — runtime-accessible Kotlin constants
  - **`gradle/generated-config.properties`** — build properties used to select product flavor variants

After editing this file, always regenerate the configuration:

```bash
./gradlew generateVonageConfig
```

> **Never hand-edit** `gradle/generated-config.properties` — it is always overwritten by `generateVonageConfig`.

### Feature flavor variants

Some features use product flavors with `enabled`/`disabled` variants (for example, the `vonage-feature-chat` module). The correct variant is selected automatically based on the value in `app-config.json`; no manual flavor selection is needed.

For a deep dive into the full configuration plugin system (JSON schema, generated constants, multi-config setups, and troubleshooting), see [CONFIG-SYSTEM.md](CONFIG-SYSTEM.md).

### Field reference

`app-config.json` follows a **shared cross-platform schema** that is also used by the React app, so
it contains a few fields that no Android code reads. These are kept deliberately, to keep the file
valid against the shared schema and interchangeable between platforms — they are **not** oversights,
and setting them has no effect on Android.

| Field | Effect on Android |
|---|---|
| `videoSettings.allowBackgroundEffects` | Background blur / virtual backgrounds |
| `videoSettings.allowCameraControl` | Camera toggle in waiting room and call |
| `videoSettings.allowVideoOnJoin` | Whether video starts enabled |
| `videoSettings.defaultResolution` | **Not implemented — parity only.** Capture resolution is chosen by the SDK per device, or by the user in the in-call settings panel. |
| `audioSettings.allowAdvancedNoiseSuppression` | Noise suppression toggle |
| `audioSettings.allowAudioOnJoin` | Whether audio starts enabled |
| `audioSettings.allowMicrophoneControl` | Mic toggle in waiting room and call |
| `audioSettings.allowAudioDiagnostics` | "Test speakers" control in the audio device selector |
| `authSettings.allowAuthentication` | Okta sign-in — see [AUTHENTICATION.md](AUTHENTICATION.md). Android-specific; not part of the shared schema. |
| `waitingRoomSettings.allowDeviceSelection` | Device selection panel in the waiting room |
| `waitingRoomSettings.allowSettings` | Settings entry point in the waiting room top bar |
| `waitingRoomSettings.bypassWaitingRoom` | **Not implemented — parity only.** The waiting room also captures the display name, publisher preferences and permissions, so it is always shown. |
| `meetingRoomSettings.allowSettings` | In-call settings panel |
| `meetingRoomSettings.allowArchiving` | Session recording |
| `meetingRoomSettings.allowCaptions` | Live captions |
| `meetingRoomSettings.allowChat` | In-call chat |
| `meetingRoomSettings.allowDeviceSelection` | Audio output selector in the call |
| `meetingRoomSettings.allowEmojis` | Emoji reactions |
| `meetingRoomSettings.allowFeedback` | "Report an issue" control |
| `meetingRoomSettings.allowPictureInPicture` | Enter picture-in-picture when leaving the app mid-call |
| `meetingRoomSettings.allowScreenShare` | Screen sharing |
| `meetingRoomSettings.defaultLayoutMode` | Layout on entering the room: `grid` or `activespeaker` |
| `meetingRoomSettings.showParticipantList` | Participant list button |
| `metadata` | Describes the config document itself; generates nothing |

### Authentication (Okta)

`authSettings.allowAuthentication` (default `false`) compiles in the optional `vonage-feature-okta` module and shows a sign-in entry point on the landing screen. It additionally requires Okta OIDC credentials from `local.properties` or environment variables — see [AUTHENTICATION.md](AUTHENTICATION.md).

## Theme customization

The app's visual theme is driven by `config/theme.json`. Edit this file with your desired color scheme values:

```bash
# Edit the theme
vim config/theme.json

# Regenerate theme resources
./gradlew generateVonageConfig
```

The `ThemeGeneratorPlugin` Gradle plugin reads `theme.json` at build time and generates the necessary theme resources into `vonage-video-ui-compose/src/main/java/com/vonage/android/compose/theme/`.

> Do **not** edit the generated theme files directly — they are overwritten on every build.

## Base API URL

The backend URL can be set in three ways (evaluated in priority order):

1. `local.properties` key `BASE_API_URL` (recommended for local development)
2. Environment variable `BASE_API_URL` (recommended for CI/CD)
3. Direct placeholder replacement in `config/app-config.json`

See [GETTING_STARTED.md](GETTING_STARTED.md) for full setup instructions.
