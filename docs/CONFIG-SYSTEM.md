# Vonage Video App Configuration System

A JSON-based configuration system that generates Kotlin constants and Gradle build configuration for modular Android apps.

## Overview

- **AppConfig**: Runtime accessible constants from JSON
- **BuildConfig**: Compile-time feature flags and product flavors
- **Modular Architecture**: Conditional feature module inclusion

## Quick Start

### 1. JSON Configuration (`config/app-config.json`)

```json
{
  "videoSettings": {
    "allowBackgroundEffects": true,
    "defaultResolution": "640x480"
  },
  "meetingRoomSettings": {
    "allowChat": true,
    "allowScreenShare": true,
    "defaultLayoutMode": "grid"
  }
}
```

### 2. Plugin Setup (`app/build.gradle.kts`)

```kotlin
plugins {
    id("com.vonage.json-config")
}

jsonConfig {
    configFile.set("config/app-config.json")
}
```

### 3. Usage in Code

Runtime config is read through the `Config` snapshot rather than the generated constants
directly, so `AppConfig` is read in exactly one place (`Config.fromAppConfig()`):

```kotlin
// In a ViewModel — inject GetConfig
class MyViewModel @Inject constructor(private val getConfig: GetConfig) {
    fun start() {
        if (getConfig().allowChat) initializeChatFeature()
    }
}

// In a composable that cannot receive an injected dependency
val config = remember { Config.fromAppConfig() }
if (config.allowChat) { /* ... */ }
```

`BuildConfig.FEATURE_*_ENABLED` fields are also generated, but they are **informational only** —
useful for logging and support. The actual compile-time gating is done by the product flavor
selected via `missingDimensionStrategy`, which swaps in the `enabled` or `disabled` source set. Do
not branch on `BuildConfig.FEATURE_*` in runtime code: the flavor system has already removed the
disabled implementation, so the check is redundant.

## Module Architecture

```
app/                          # Main application
├── shared/                   # Common utilities
├── kotlin/                   # Core SDK integration  
├── compose/                  # UI components
└── vonage-feature-chat/     # Optional chat feature
    ├── enabled/             # Full implementation
    └── disabled/            # Stub implementation
```

## Generated Files

1. **AppConfig.kt** - Runtime constants
2. **BuildConfig.java** - Compile-time feature flags
3. **gradle/generated-config.properties** - Build properties

## Configuration Management

### Switch Configurations

```bash
# Method 1: Edit JSON directly
vim config/app-config.json

# Method 2: Use different config file
./gradlew assembleDebug -Dconfig.file=config/app-config-minimal.json

# Method 3: Regenerate after changes
./gradlew clean generateVonageConfig assembleDebug
```

### Feature Module Setup

```kotlin
// vonage-feature-chat/build.gradle.kts
flavorDimensions += "chat"
productFlavors {
    create("enabled") { dimension = "chat" }
    create("disabled") { dimension = "chat" }
}

// app/build.gradle.kts - automatic selection
defaultConfig {
    val chatProperty = configProps.getProperty("vonage.meetingRoom.allow_chat", "false")
    missingDimensionStrategy("chat", chatProperty.toEnabledString())
}
```

## Key Configuration Properties

`AppConfig` lists the generated constant; `BuildConfig`/flavor shows whether the field also selects
a product flavor. "Wired" describes how the value reaches behavior.

| Setting | AppConfig | Flavor dimension | Wired |
|---------|-----------|------------------|-------|
| `meetingRoomSettings.allowChat` | `ALLOW_CHAT` | `chat` | Flavor + `MeetingRoomFeature.CHAT` |
| `meetingRoomSettings.allowScreenShare` | `ALLOW_SCREEN_SHARE` | `screensharing` | Flavor + `MeetingRoomFeature.SCREEN_SHARE` |
| `meetingRoomSettings.allowPictureInPicture` | `ALLOW_PICTURE_IN_PICTURE` | - | `MeetingRoomConfiguration.allowPictureInPicture` |
| `meetingRoomSettings.defaultLayoutMode` | `DEFAULT_LAYOUT_MODE` | - | `MeetingRoomConfiguration.defaultLayoutMode` |
| `videoSettings.defaultResolution` | `DEFAULT_RESOLUTION` | - | **Not implemented** — parity only |
| `waitingRoomSettings.bypassWaitingRoom` | `BYPASS_WAITING_ROOM` | - | **Not implemented** — parity only |

See `docs/CONFIGURATION.md` for the full field-by-field table.

## Adding a new config field

The generator only understands the keys declared in `SETTINGS_GROUPS`, `SCALAR_KEYS` and
`IGNORED_KEYS` in `GenerateConfigTask`. Adding a field **inside** an existing settings group works
automatically. Adding a **new top-level key** fails the build with a message naming the key, so a
new group cannot silently generate nothing:

```
Unrecognised top-level key(s) in the app config: recordingSettings
```

Extend `SETTINGS_GROUPS` (for an object), `SCALAR_KEYS` (for a single value), or `IGNORED_KEYS` (for
a documentation-only block).

## Best Practices

- **Config**: Runtime settings read via `GetConfig` / `Config.fromAppConfig()`
- **BuildConfig `FEATURE_*`**: Informational only — do not branch on these
- **Product Flavors**: The real compile-time gating for feature modules with enabled/disabled variants

## Troubleshooting

```bash
# Force regeneration
./gradlew clean generateVonageConfig

# Check generated files
cat app/build/generated/source/jsonConfig/com/vonage/android/config/AppConfig.kt
cat gradle/generated-config.properties

# Verify task execution
./gradlew generateVonageConfig --info
```