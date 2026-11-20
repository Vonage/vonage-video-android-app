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
| `CAMERA`, `RECORD_AUDIO` | `vonage-meeting-room`, SDK | Publishing; requested in `CallPermissionHandler` and `MeetingRoomRequiredPermissions` | Keep |
| `MODIFY_AUDIO_SETTINGS` | SDK | SDK default audio device | Keep |
| `BLUETOOTH_CONNECT` | `vonage-meeting-room`, SDK | `VeraBluetoothManager` (`vonage-audio-selector`) on API 31+ | Keep |
| `BLUETOOTH` | SDK | SDK default audio device on API 30 and below | Keep |
| `ACCESS_NETWORK_STATE` | SDK, Firebase | WebRTC `NetworkMonitorAutoDetect`; required by the SDK since 2.29.1 | Keep |
| `READ_PHONE_STATE` | SDK | SDK call-interruption handling on API 31+. Never requested at runtime, so currently never granted | Keep for now, tracked as a follow-up |
| `POST_NOTIFICATIONS` | `app`, `vonage-meeting-room` | Ongoing-call and screen-share notifications; requested on API 33+ | Keep |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MICROPHONE`, `FOREGROUND_SERVICE_CAMERA` | `app`, `vonage-meeting-room` | Call service declared with `foregroundServiceType="microphone\|camera"` | Keep |
| `FOREGROUND_SERVICE_MEDIA_PROJECTION` | `vonage-feature-screensharing` (`enabled` flavor) | `ScreenSharingService` | Keep |
| `SYSTEM_ALERT_WINDOW` | `vonage-feature-screensharing` (`enabled` flavor) | `EnabledScreenSharing.canDrawOverlays()` for the screen-share overlay | Keep |
| `WRITE_EXTERNAL_STORAGE` (`maxSdkVersion="28"`) | `app` | `util/DownloadManager.kt`: `setDestinationInExternalPublicDir` needs it only up to API 28 | Keep, limited to API 28 |
| `WAKE_LOCK`, `BIND_GET_INSTALL_REFERRER_SERVICE`, `ACCESS_ADSERVICES_ATTRIBUTION`, `ACCESS_ADSERVICES_AD_ID` | Firebase Analytics (release only) | Firebase measurement and ad attribution. `AD_ID` is already removed in `app/src/release/AndroidManifest.xml` | Keep for now, tracked as a follow-up |
| `<applicationId>.DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION` | `androidx.core` | `ContextCompat.registerReceiver` | Keep |

Debug builds also get `READ_EXTERNAL_STORAGE` from LeakCanary. Release builds don't.

Removed in VIDSOL-1748: `VIBRATE` from `app/src/debug/AndroidManifest.xml`. Nothing used it: StrictMode only logs, and Compose haptic feedback doesn't need the permission.

## Network permissions not declared

| Permission | Status | Reason |
|---|---|---|
| `CHANGE_NETWORK_STATE` | Not declared. Don't add | Only WebRTC's `NetworkMonitorAutoDetect.requestMobileNetwork` would use it, and it catches the `SecurityException`. Without it WebRTC uses the default network only and doesn't keep cellular up as a backup while on Wi-Fi. |
| `ACCESS_WIFI_STATE` | Not declared. Don't add | WebRTC reads the SSID from the network-state broadcast, which doesn't need this permission. Its Wi-Fi Direct path is behind a WebRTC field trial that is off by default. No app code uses `WifiManager`. |
| `ACCESS_LOCAL_NETWORK` | Not declared. Don't add yet | The [SDK 2.35.1 release notes](https://developer.vonage.com/en/opentok/client-sdk/android/release-notes) say Android 17 doesn't require it. The [Android documentation](https://developer.android.com/privacy-and-security/local-network-permission) says UDP and TCP traffic to LAN addresses is blocked without it for apps targeting API 37. Worst case for calls: peers on the same LAN can't use host candidates and fall back to STUN/TURN. Debug builds whose `BASE_API_URL` points to a LAN address may also be blocked. Pending verification on an API 37 device. |

## Encrypted Client Hello (ECH)

Decision: **keep as is**. No network security config change is needed.

- The app has no `network_security_config.xml`. Both manifests set `usesCleartextTraffic="false"`.
- On Android 17, ECH is on by default for libraries that support it, unless a network security config turns it off with `domainEncryption` ([Android docs](https://developer.android.com/privacy-and-security/encrypted-client-hello)).
- OkHttp 5.5.1 supports ECH only when the client uses `AndroidDns` or `DnsOverHttps`. `VeraApiClient.Builder.build()` uses OkHttp's default DNS, so backend calls don't use ECH today.
- Media and signaling go through the SDK's bundled WebRTC TLS stack, which the platform ECH setting doesn't affect.

Turning ECH on for backend calls is optional and only helps if the backend domain publishes an HTTPS DNS record with an ECH config.

## WebView

Decision: **no WebView dependency, no version constraint**.

- No code in the app or its libraries creates a `WebView`.
- The only `android.webkit` references in the APK are `MimeTypeMap` (in `androidx.core` `FileProvider` and in LeakCanary, debug only) and `WebView.findAddress` in `androidx.core` `LinkifyCompat`. The app never calls `LinkifyCompat`; it uses its own `vonage-video-ui-compose` `linkify` package.
- Okta sign-in (`vonage-feature-okta`, off by default) opens the browser through Custom Tabs (`androidx.browser`), not a WebView.

## Open follow-ups

- Verify `ACCESS_LOCAL_NETWORK` behaviour on an API 37 device: a same-LAN relayed call, and a debug build pointing at a LAN or `10.0.2.2` backend.
- `READ_PHONE_STATE`: either request it at runtime on API 31+ or remove it with `tools:node="remove"`.
- Firebase ad attribution permissions: remove them like `AD_ID` if we don't use attribution.
- Recording download on API 24–28: `WRITE_EXTERNAL_STORAGE` is never requested at runtime, so `DownloadManager` may fail there.
- `CallPermissionHandler` requests `BLUETOOTH_CONNECT` from API 33. It should start at API 31, as `MeetingRoomRequiredPermissions` does.
- The SDK docs list `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` as required on API 35+, but the 2.36.0 AAR doesn't declare it. Confirm with the SDK team.
- Optional: enable ECH for `VeraApiClient` through `AndroidDns` once the backend publishes ECH config.
