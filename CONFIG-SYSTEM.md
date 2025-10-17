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
    "defaultLayoutMode": "active-speaker"
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

```kotlin
// Runtime configuration
if (AppConfig.MeetingRoomSettings.ALLOW_CHAT) {
    initializeChatFeature()
}

// Build-time optimization
if (BuildConfig.FEATURE_CHAT_ENABLED) {
    // Only included if chat is enabled
    initializeChatModule()
}
```

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

| Setting | AppConfig | BuildConfig | Description |
|---------|-----------|-------------|-------------|
| `meetingRoomSettings.allowChat` | `ALLOW_CHAT` | `FEATURE_CHAT_ENABLED` | Chat module & functionality |
| `videoSettings.defaultResolution` | `DEFAULT_RESOLUTION` | - | Video resolution setting |
| `meetingRoomSettings.allowScreenShare` | `ALLOW_SCREEN_SHARE` | - | Screen sharing capability |

## Best Practices

- **AppConfig**: Runtime settings, user preferences, server-driven features
- **BuildConfig**: Module inclusion, compile-time optimizations
- **Product Flavors**: Feature modules with enabled/disabled variants

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