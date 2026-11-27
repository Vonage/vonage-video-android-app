# Getting Started

> **Running the app for the first time?** The [README](../README.md#running-locally) covers the same path end to end, including feature flags, theme customization, and troubleshooting. This guide is the step-by-step reference for each stage.

## Prerequisites

- **Android Studio**: a release [compatible with AGP 9.5](https://developer.android.com/build/releases/gradle-plugin#android_gradle_plugin_and_android_studio_compatibility)
- **JDK**: Version 17 or higher
- **Gradle**: provided by the wrapper (`./gradlew`); no separate install needed

A deployed backend is required. Follow the steps to create a Vonage account, application, and backend deployment at the [vonage-video-react-app](https://github.com/Vonage/vonage-video-react-app?tab=readme-ov-file#running-locally) repository before proceeding.

## Clone the repository

```bash
git clone https://github.com/Vonage/vonage-video-android-app.git
cd vonage-video-android-app
```

## Configure the backend URL

The app needs to know the URL of your deployed backend. There are three ways to set it (evaluated in priority order):

**Option 1 — `local.properties`** (recommended for local development):

```properties
BASE_API_URL=https://your-backend-url.com
```

**Option 2 — Environment variable** (recommended for CI/CD):

```bash
export BASE_API_URL=https://your-backend-url.com
```

**Option 3 — Edit `app-config.json`** directly, replacing the placeholder with your URL.

> **Emulator tip:** If your backend is running locally, use the special alias `10.0.2.2` instead of `localhost` so the emulator can reach your machine. On Android 17 (API 37) and later, the app can't reach `10.0.2.2` without the local network permission. See [Permissions](PERMISSIONS.md#local-backend-on-android-17) for workarounds.

## Regenerate configuration

After setting the URL, regenerate the build configuration:

```bash
./gradlew generateVonageConfig
```

> This step is required after any change to `app-config.json`. Changes to `theme.json` are applied with `./gradlew updateTheme` instead. The generated file at `gradle/generated-config.properties` must **not** be hand-edited.

## Build and install

Open the project in Android Studio and let Gradle sync complete, then run:

```bash
./gradlew installDebug
```

Or use Android Studio's run button (▶️) to build and deploy to a connected device or emulator.

## Further configuration

- [Feature configuration and theme customization](CONFIGURATION.md)
- [Full config system reference](CONFIG-SYSTEM.md)
