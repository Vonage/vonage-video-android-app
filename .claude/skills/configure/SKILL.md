---
name: configure
description: Change how the Vonage Video Android reference app (VERA) is configured without writing Kotlin — toggle existing features, change the default layout, brand colours/typography via theme.json, point the app at a backend, or swap config files. Use whenever the user edits app-config.json or theme.json, asks to turn chat/captions/recording/screen share/reactions/background effects/settings/sign-in on or off, wants to rebrand or recolour the app, asks why a config change "isn't taking effect", or mentions generateVonageConfig, updateTheme, the Vonage tool window in Android Studio, or the config TUI. For ADDING a brand-new config key use the feature-flag guide instead.
inclusion: fileMatch
fileMatchPattern: '*.json'
---

# Configuring VERA Android (existing keys)

In kiro this attaches automatically when `app-config.json` or `theme.json` is in context; pull it in with `#vera-configure` otherwise. Adding a key that doesn't exist yet is a different job: `/feature-flag` in Claude Code, `#vera-feature-flags` in kiro.

`app-config.json` and `theme.json` at the repo root are the source of truth. Nothing in Kotlin should be edited to flip a feature; if the user is reaching for a `BuildConfig` or `AppConfig` constant to change behaviour, redirect them to the JSON.

## Pick the editing tool

| Tool | When | What it does for you |
|---|---|---|
| **Android Studio plugin** (`vonage-config-idea-plugin`, tool window **View → Tool Windows → Vonage**) | Day-to-day toggling inside the IDE | Tree view of `app-config.json`; on change runs `./gradlew clean generateVonageConfig` and streams output. Theme tab is preview-only. Build/install: `./gradlew :vonage-config-idea-plugin:buildPlugin` → `build/distributions/*.zip` → *Install Plugin from Disk*. Needs a JDK 21 to build. |
| **Config TUI** (`tools/config-tui-py`) | Terminal users, validating against the shared schema | Form editor for both files, live schema validation (schemas fetched from the React repo, cached), runs `generateVonageConfig` / `generateTheme` after save, "Launch App" = `installDebug`. `pipx install ./tools/config-tui-py && vonage-config`. |
| **By hand** | Scripts, CI, quick edits | Edit JSON, then run the regenerate commands below yourself. |

## `app-config.json` → behaviour

Every key travels this path; knowing it is how you debug "the flag does nothing":

1. `app-config.json` → `./gradlew generateVonageConfig` (also runs eagerly on every Gradle sync) →
2. `gradle/generated-config.properties` (`vonage.<group>.<snake_case_key>`; generated, gitignored, never hand-edit) and `AppConfig.kt` (`app/build/generated/source/jsonConfig/…`, `AppConfig.MeetingRoomSettings.ALLOW_CHAT` etc.) →
3. `app/build.gradle.kts` reads the properties: for flavor-backed keys it calls `missingDimensionStrategy("<dimension>", "enabled"|"disabled")`, which swaps the `src/enabled` / `src/disabled` source set of the matching `vonage-feature-*` module; it also emits `BuildConfig.FEATURE_*_ENABLED`, which is **informational only** — never branch on it →
4. `Config.fromAppConfig()` (`app/src/main/java/com/vonage/android/config/Config.kt`) is the single place `AppConfig` is read; ViewModels inject `GetConfig`, composables call `Config.fromAppConfig()` →
5. `MeetingRoomScreenRoute.kt` maps `Config` to the meeting room: `configuredMeetingRoomFeatures()` builds the runtime `Set<MeetingRoomFeature>` passed to `MeetingRoomBuilder.enabledFeatures(...)`, and the UI-control toggles go into `MeetingRoomConfiguration`.

Two gates, both must be open: a feature is active only when its **compile-time flavor is `enabled`** AND it is **in the runtime set**. You cannot re-enable a flavor-disabled feature at runtime. Inside `vonage-meeting-room` itself, chat/reactions/videofx/audiofx/settings always resolve to `enabled` (its `defaultConfig` pins them) — for those, the runtime set is the only thing that turns them off; archiving/captions/screensharing additionally have real `archivingEnabled`/`…Disabled` flavors in that module.

### Which keys are flavor-backed

| Key | Flavor dimension | Runtime gate |
|---|---|---|
| `meetingRoomSettings.allowChat` | `chat` | `MeetingRoomFeature.CHAT` |
| `meetingRoomSettings.allowEmojis` | `reactions` | `MeetingRoomFeature.REACTIONS` |
| `meetingRoomSettings.allowArchiving` | `archiving` (+ `archivingEnabled` in meeting-room) | `MeetingRoomFeature.ARCHIVING` |
| `meetingRoomSettings.allowCaptions` | `captions` (+ `captionsEnabled`) | `MeetingRoomFeature.CAPTIONS` |
| `meetingRoomSettings.allowScreenShare` | `screensharing` (+ `screensharingEnabled`) | `MeetingRoomFeature.SCREEN_SHARE` |
| `videoSettings.allowBackgroundEffects` | `videofx` | `MeetingRoomFeature.BACKGROUND_EFFECTS` |
| `audioSettings.allowAdvancedNoiseSuppression` | `audiofx` | `MeetingRoomFeature.AUDIO_EFFECTS` |
| `meetingRoomSettings.allowSettings` | `settings` | `MeetingRoomConfiguration.allowSettings` (no `MeetingRoomFeature` case) |
| `authSettings.allowAuthentication` | `okta` — also raises `minSdk` to 26 | n/a (landing-screen sign-in; Android-only key, not in the shared schema) |

Everything else (`allowCameraControl`, `allowMicrophoneControl`, `allowAudioOnJoin`, `allowVideoOnJoin`, `allowDeviceSelection`, `allowFeedback`, `allowPictureInPicture`, `showParticipantList`, `defaultLayoutMode` = `grid` | `activeSpeaker`, …) is runtime-only via `Config`/`MeetingRoomConfiguration`. Two keys exist purely for cross-platform parity and do nothing on Android: `videoSettings.defaultResolution` and `waitingRoomSettings.bypassWaitingRoom`. `metadata` generates nothing. The full table is in `docs/CONFIGURATION.md`.

### Apply a change

```bash
./gradlew generateVonageConfig        # or just Sync in Android Studio / let the plugin do it
./gradlew installDebug
```

If the change flips a flavor-backed key, Android Studio must **re-sync** so the IDE indexes the right source set; a CLI build picks it up on its own because the plugin regenerates at configuration time.

Alternative config file without touching the default: `./gradlew assembleDebug -Dconfig.file=/abs/path/other-config.json`.

### Validation

`app-config.json` and `theme.json` are validated in CI (`.github/workflows/validate-config-schemas.yml`) against the shared schemas in the React repo:
`https://raw.githubusercontent.com/Vonage/vonage-video-react-app/develop/specs/app-config.schema.json` and `…/theme.schema.json`. Locally the TUI validates on the fly, or:

```bash
curl -fsSL https://raw.githubusercontent.com/Vonage/vonage-video-react-app/develop/specs/app-config.schema.json -o /tmp/app-config.schema.json
npx ajv-cli@5 validate --spec=draft2020 --strict=false -s /tmp/app-config.schema.json -d app-config.json
```

The settings groups accept extra **boolean** keys only, so a new string/number key inside a group fails CI until the shared schema changes.

## `theme.json` → Compose theme

`theme.json` drives colours (light/dark), border radii and typography. The generator is `com.vonage.theme-generator`, applied in `vonage-video-ui-compose`, and — unlike the config — **its output is committed source** under `vonage-video-ui-compose/src/main/java/com/vonage/android/compose/theme/` (files headed `// Auto-generated from theme.json`: `Color.kt`, `Typography.kt`, `Shape.kt`, `Theme.kt`).

```bash
./gradlew updateTheme      # == :vonage-video-ui-compose:generateTheme; NOT part of generateVonageConfig
```

Then review the diff, commit the regenerated files with `theme.json`, and expect the Roborazzi goldens to change — re-record them (`/record-snapshots`, or `#vera-snapshot-tests` in kiro). Several docs say `generateVonageConfig` regenerates the theme; it does not, trust the task graph.

Hosts embedding only the `vonage-meeting-room` library don't use `theme.json`; they pass `MeetingRoomBuilder.theme(MeetingRoomTheme(...))`.

## Backend URL

`baseApiUrl` in `app-config.json` stays `${BASE_API_URL}`; the value comes from `local.properties` or the `BASE_API_URL` environment variable (env wins). The generator fails without one. It reaches three places: Retrofit base URL (`RetrofitModule.kt`), the App Link host (`manifestPlaceholders["hostName"]`, so deep links into `AppNavHost.kt`), and share links (`util/navigateToShare.kt`). Emulator → host machine: `10.0.2.2`.

## "My change isn't taking effect" — checklist

1. Did `generateVonageConfig` run (or a sync) *after* the edit? Check `gradle/generated-config.properties` for the new value.
2. Was `gradle/generated-config.properties` or `AppConfig.kt` edited by hand? Both are overwritten.
3. Is the key one of the parity-only ones (`defaultResolution`, `bypassWaitingRoom`)? Then nothing reads it — by design.
4. Expected a compile-time effect from a runtime-only key (or vice versa)? See the table.
5. Looking at `BuildConfig.FEATURE_*`? Informational; the flavor source set is what changed.
6. Flavor-backed key flipped but Android Studio still shows the old implementation? Re-sync.
7. Theme edit? Needs `updateTheme`, not `generateVonageConfig`.
