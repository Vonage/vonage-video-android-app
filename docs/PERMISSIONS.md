# Permissions and Platform Dependencies

This page lists every permission in the release APK's merged manifest, the library that adds it, and the code that uses it. It also records decisions on the network permissions and platform features introduced by Android 17 (API 37), which this app targets.

Last audited for VIDSOL-1748 against `targetSdk = 37` and Vonage Video SDK `2.36.0`.

## How to re-check

```bash
./gradlew :app:processReleaseManifest
# Merged result
cat app/build/intermediates/merged_manifests/release/processReleaseManifest/AndroidManifest.xml
# Which manifest added each entry
grep -A2 "^uses-permission#" app/build/outputs/logs/manifest-merger-release-report.txt
```

To find which library calls a platform API, disassemble the APK with `dexdump -d` from the Android build tools and search for the class descriptor (for example `Landroid/webkit/`).

## Declared permissions

| Permission | Added by | Used by | Decision |
|---|---|---|---|
| `INTERNET` | `app`, `vonage-meeting-room`, SDK | Backend API (`VeraApiClient`) and media | Keep |
| `CAMERA`, `RECORD_AUDIO` | `vonage-meeting-room`, SDK | Publishing; requested by `callPermissions()` (`CallPermissionHandler.kt`) and `MeetingRoomRequiredPermissions` | Keep |
| `MODIFY_AUDIO_SETTINGS` | SDK | SDK default audio device | Keep |
| `BLUETOOTH_CONNECT` | `vonage-meeting-room`, SDK | `VeraBluetoothManager` (`vonage-audio-selector`); requested on API 31+ | Keep |
| `BLUETOOTH` | SDK | SDK default audio device on API 30 and below | Keep |
| `ACCESS_NETWORK_STATE` | SDK, Firebase | WebRTC `NetworkMonitorAutoDetect`; required by the SDK since 2.29.1 | Keep |
| `POST_NOTIFICATIONS` | `app`, `vonage-meeting-room` | Ongoing-call and screen-share notifications; requested on API 33+ | Keep |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MICROPHONE`, `FOREGROUND_SERVICE_CAMERA` | `app`, `vonage-meeting-room` | Call service declared with `foregroundServiceType="microphone\|camera"` | Keep |
| `FOREGROUND_SERVICE_MEDIA_PROJECTION` | `vonage-feature-screensharing` (`enabled` flavor) | `ScreenSharingService` | Keep |
| `SYSTEM_ALERT_WINDOW` | `vonage-feature-screensharing` (`enabled` flavor) | `EnabledScreenSharing.canDrawOverlays()` for the screen-share overlay | Keep |
| `WAKE_LOCK` | Firebase Analytics (release only) | Firebase measurement upload scheduling | Keep |
| `<applicationId>.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | `androidx.core` | `ContextCompat.registerReceiver` | Keep |

Debug builds also get `READ_EXTERNAL_STORAGE` and `WRITE_EXTERNAL_STORAGE` from LeakCanary (`debugImplementation` only). Release builds don't.

Removed in VIDSOL-1748:

- `VIBRATE` (`app/src/debug/AndroidManifest.xml`). Nothing used it: StrictMode only logs, and Compose haptic feedback doesn't need the permission.
- `WRITE_EXTERNAL_STORAGE`. Only `util/DownloadManager.kt` needed it, to save to the public Downloads folder on API 28 and below, and the app never requested it at runtime. On API 28 and below, downloads now go to the app-specific Downloads folder, which needs no permission. API 29+ still uses the public Downloads folder.
- `READ_PHONE_STATE`, which the SDK declares, removed with `tools:node="remove"` in `app/src/main/AndroidManifest.xml`. The SDK uses it for call-interruption handling on API 31+, but the app never requested it, so it was never granted and removing it changes nothing at runtime. `vonage-meeting-room` keeps the SDK default so host apps can decide.
- `ACCESS_ADSERVICES_AD_ID`, `ACCESS_ADSERVICES_ATTRIBUTION` and `BIND_GET_INSTALL_REFERRER_SERVICE`, which Firebase Analytics declares, removed in `app/src/release/AndroidManifest.xml` next to the existing `AD_ID` removal. They only serve ad and install attribution. The app doesn't call the Analytics API and relies only on automatic collection.

## Permissions intentionally not declared

| Permission | Status | Reason |
|---|---|---|
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Not declared. Don't add | The [SDK docs](https://tokbox.com/developer/sdks/android) list it as required on API 35+, but the 2.36.0 AAR doesn't declare it. On API 35+, `Session.connect` opens the system "ignore battery optimizations" dialog only if the `Context` passed to `Session.Builder` is an `Activity`. `SdkModule` and `MeetingRoomContainer` both pass the application context, so the dialog never opens. Google Play also restricts this permission to specific app categories. |
| `CHANGE_NETWORK_STATE` | Not declared. Don't add | Only WebRTC's `NetworkMonitorAutoDetect.requestMobileNetwork` would use it, and it catches the `SecurityException`. Without it WebRTC uses the default network only and doesn't keep cellular up as a backup while on Wi-Fi. |
| `ACCESS_WIFI_STATE` | Not declared. Don't add | WebRTC reads the SSID from the network-state broadcast, which doesn't need this permission. Its Wi-Fi Direct path is behind a WebRTC field trial that is off by default. No app code uses `WifiManager`. |
| `ACCESS_LOCAL_NETWORK` | Not declared. Don't add | The [SDK 2.35.1 release notes](https://developer.vonage.com/en/opentok/client-sdk/android/release-notes) say Android 17 doesn't require it. The [Android documentation](https://developer.android.com/privacy-and-security/local-network-permission) says that, for apps targeting API 37, TCP and UDP traffic to LAN addresses is blocked without it. Worst case for calls: peers on the same LAN can't use host candidates and fall back to STUN/TURN. On an API 37 emulator, the app's UID couldn't reach `10.0.2.2` (the emulator's alias for the host machine) until `ACCESS_LOCAL_NETWORK` was declared and granted. A local backend at `10.0.2.2` or a LAN IP therefore doesn't work on Android 17 without it (see *Local backend on Android 17* below). |

### Local backend on Android 17

On an Android 17 device or emulator, a debug build can't reach a backend at `10.0.2.2` or a LAN IP: TCP connections time out. Either expose the backend through an HTTPS tunnel or temporarily add `<uses-permission android:name="android.permission.ACCESS_LOCAL_NETWORK" />` to `app/src/debug/AndroidManifest.xml` and install with `adb install -g`. Don't commit the permission: release builds don't need it.

### Publishing on the API 37 emulator

On the `android-37.0` Google APIs arm64 emulator image, joining a room on `meet.vonagenetworks.net` connects the session but publishing fails with "Timed out while attempting to publish". This happens with and without `ACCESS_LOCAL_NETWORK` granted, so the local network permission isn't the cause. The same APK publishes normally on an API 34 emulator. On a physical Pixel 9a running Android 17, the same build joins and publishes normally, without `ACCESS_LOCAL_NETWORK`. The failure is specific to the emulator image, so use a physical device or an older emulator image to test calls.

## Encrypted Client Hello (ECH)

Decision: **keep as is**. No network security config change is needed.

- The app has no `network_security_config.xml`. Both manifests set `usesCleartextTraffic="false"`.
- On Android 17, ECH is on by default for libraries that support it, unless a network security config turns it off with `domainEncryption` ([Android docs](https://developer.android.com/privacy-and-security/encrypted-client-hello)).
- OkHttp 5.5.1 supports ECH only when the client uses `AndroidDns` or `DnsOverHttps`. `VeraApiClient.Builder.build()` uses OkHttp's default DNS, so backend calls don't use ECH today.
- Media and signaling go through the SDK's bundled WebRTC TLS stack, which the platform ECH setting doesn't affect.
- The backend host, `meet.vonagenetworks.net`, is a CNAME to an AWS Elastic Load Balancer with no HTTPS (type 65) DNS record, so it publishes no ECH config. Checked with `dig meet.vonagenetworks.net TYPE65 @1.1.1.1`.

Switching `VeraApiClient` to `AndroidDns` would add only ECH GREASE (placeholder ECH data) and no privacy benefit, so it isn't worth doing now. Revisit if the backend moves behind a front end that publishes ECH config.

## WebView

Decision: **no WebView dependency, no version constraint**.

- No code in the app or its libraries creates a `WebView`.
- The only `android.webkit` references in the APK are `MimeTypeMap` (in `androidx.core` `FileProvider` and in LeakCanary, debug only) and `WebView.findAddress` in `androidx.core` `LinkifyCompat`. The app never calls `LinkifyCompat`; it uses its own `vonage-video-ui-compose` `linkify` package.
- Okta sign-in (`vonage-feature-okta`, off by default) opens the browser through Custom Tabs (`androidx.browser`), not a WebView.

## Open follow-ups

- Same-LAN relayed call on two Android 17 devices, to confirm the SDK's claim that `ACCESS_LOCAL_NETWORK` isn't needed.
