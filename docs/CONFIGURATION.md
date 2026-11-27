# Configuration

## Feature configuration

You can fork the repository and start modifying it for your needs, or configure features via the JSON files at the root of the repository without touching source code.

### `app-config.json`

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

> **Prefer a form editor?** Two editors write to the same files and run the regeneration task for you:
> - The config TUI in [`tools/config-tui-py`](../tools/config-tui-py/README.md) edits `app-config.json` and `theme.json`, validates them against the shared cross-platform schemas, and regenerates after each save.
> - The Android Studio plugin in [`vonage-config-idea-plugin`](../vonage-config-idea-plugin/README.md) shows `app-config.json` in a **Vonage** tool window and runs `./gradlew clean generateVonageConfig` when the file changes. It only previews `theme.json`, so theme changes still need `./gradlew updateTheme`.

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
| `meetingRoomSettings.allowArchiving` | Session recording. Independent of `allowPostCallTranscription`. |
| `meetingRoomSettings.allowCaptions` | Live captions |
| `meetingRoomSettings.allowCaptionTranslation` | Captions translation language picker in the settings screen (Captions section); shown only when `allowCaptions` is also `true`. Translation runs per viewer through the Video SDK; the Vonage project must have caption translation enabled (private beta), and the backend's `CAPTIONS_TRANSLATION_LEVEL` controls how partial captions are translated. |
| `meetingRoomSettings.allowChat` | In-call chat |
| `meetingRoomSettings.allowDeviceSelection` | Audio output selector in the call |
| `meetingRoomSettings.allowEmojis` | Emoji reactions |
| `meetingRoomSettings.allowFeedback` | "Report an issue" control |
| `meetingRoomSettings.allowPictureInPicture` | Enter picture-in-picture when leaving the app mid-call |
| `meetingRoomSettings.allowPostCallTranscription` | Start/Stop transcription button in the meeting room (`true` in the checked-in `app-config.json`; the key must be present — omitting it fails compilation, there is no safe default for a missing key). Starts a transcription archive that runs alongside any recording. Independent of `allowArchiving`: either one can be on without the other. |
| `meetingRoomSettings.allowScreenShare` | Screen sharing |
| `meetingRoomSettings.defaultLayoutMode` | Layout on entering the room: `grid` or `activespeaker` |
| `meetingRoomSettings.showParticipantList` | Participant list button |
| `metadata` | Describes the config document itself; generates nothing |

### Authentication (Okta)

`authSettings.allowAuthentication` (default `false`) compiles in the optional `vonage-feature-okta` module and shows a sign-in entry point on the landing screen. It additionally requires Okta OIDC credentials from `local.properties` or environment variables — see [AUTHENTICATION.md](AUTHENTICATION.md).

## Theme customization

The app's visual theme is driven by `theme.json`. Edit this file with your desired color scheme values:

```bash
# Edit the theme
vim theme.json

# Regenerate theme resources
./gradlew updateTheme
```

The `ThemeGeneratorPlugin` Gradle plugin reads `theme.json` and generates the theme sources into `vonage-video-ui-compose/src/main/java/com/vonage/android/compose/theme/`. It does **not** run as part of a normal build or `generateVonageConfig`; run `updateTheme` after every `theme.json` change and commit the regenerated files.

> Do **not** edit the generated theme files directly — they are overwritten every time `updateTheme` runs.

## Base API URL

The backend URL can be set in three ways (evaluated in priority order):

1. `local.properties` key `BASE_API_URL` (recommended for local development)
2. Environment variable `BASE_API_URL` (recommended for CI/CD)
3. Direct placeholder replacement in `app-config.json`

See [GETTING_STARTED.md](GETTING_STARTED.md) for full setup instructions.

## Updating the Vonage Video SDK Version

The SDK version is declared once, as `opentokAndroidSdk` in `gradle/libs.versions.toml`. Both `com.vonage:client-sdk-video` and `com.vonage:client-sdk-video-transformers` reference it, so the two always stay on the same version. Do not pin the SDK version anywhere else.

1. Edit `opentokAndroidSdk` in `gradle/libs.versions.toml`.
2. Sync Gradle and run `./gradlew test` to confirm the project builds and tests pass.

This app has been tested with **Vonage Video SDK 2.32** and **2.33**; use the latest available version where possible.
