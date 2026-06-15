# Getting Started

## Prerequisites

- **Android Studio**: Ladybug (2024.2.1) or newer
- **JDK**: Version 17 or higher
- **Gradle**: 8.13.0+ (via wrapper)

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

**Option 3 — Edit `config/app-config.json`** directly, replacing the placeholder with your URL.

> **Emulator tip:** If your backend is running locally, use the special alias `10.0.2.2` instead of `localhost` so the emulator can reach your machine.

## Regenerate configuration

After setting the URL, regenerate the build configuration:

```bash
./gradlew generateVonageConfig
```

> This step is required after any change to `config/app-config.json` or `config/theme.json`. The generated file at `gradle/generated-config.properties` must **not** be hand-edited.

## Build and install

Open the project in Android Studio and let Gradle sync complete, then run:

```bash
./gradlew installDebug
```

Or use Android Studio's run button (▶️) to build and deploy to a connected device or emulator.

## Further configuration

- [Feature configuration and theme customization](CONFIGURATION.md)
- [Full config system reference](CONFIG-SYSTEM.md)
