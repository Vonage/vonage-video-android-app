# Vonage Video App Configuration System

This document explains how to use the configuration system that generates both Kotlin constants and Gradle build configuration from JSON files.

## Overview

The configuration system provides two levels of configuration:

1. **AppConfig** - Runtime accessible constants generated from JSON
2. **BuildConfig** - Compile-time build configuration with feature flags and product flavors
3. **Product Flavors** - Module-specific build variants (e.g., chat enabled/disabled)

## JSON Configuration Structure

```json
{
  "videoSettings": {
    "allowBackgroundEffects": true,
    "allowCameraControl": true,
    "allowVideoOnJoin": true,
    "defaultResolution": "640x480"
  },
  "audioSettings": {
    "allowAdvancedNoiseSuppression": true,
    "allowAudioOnJoin": true,
    "allowMicrophoneControl": true
  },
  "waitingRoomSettings": {
    "allowDeviceSelection": true
  },
  "meetingRoomSettings": {
    "allowArchiving": true,
    "allowCaptions": true,
    "allowChat": true,
    "allowDeviceSelection": true,
    "allowEmojis": true,
    "allowScreenShare": true,
    "defaultLayoutMode": "active-speaker",
    "showParticipantList": true
  }
}
```

## Architecture

The system now includes modular architecture with separate feature modules:

- **Main App** (`app/`) - Core application
- **Shared Module** (`shared/`) - Common utilities and interfaces
- **Kotlin Module** (`kotlin/`) - Core Vonage Video SDK integration
- **Compose Module** (`compose/`) - UI components
- **Chat Feature** (`vonage-feature-chat/`) - Optional chat functionality

## Usage in Code

### AppConfig (Runtime Configuration)

```kotlin
if (AppConfig.MeetingRoomSettings.ALLOW_CHAT) {
    initializeChatFeature()
}
```

### BuildConfig (Build-time Configuration)

```kotlin
// Feature flags for compile-time optimization
if (BuildConfig.FEATURE_CHAT_ENABLED) {
    // This code is optimized out if chat is disabled
    initializeChatModule()
}
```

### Product Flavors and Dimensions

The system uses product flavors to conditionally include feature modules:

```kotlin
// In app/build.gradle.kts
defaultConfig {
    val chatProperty = configProps.getProperty("vonage.meetingRoom.allow_chat", "false")
    buildConfigField("boolean", "FEATURE_CHAT_ENABLED", "$chatProperty")
    missingDimensionStrategy("chat", chatProperty.toEnabledString())
}

// Helper function converts boolean to flavor name
fun String.toEnabledString(): String = if (toBoolean()) "enabled" else "disabled"
```

## Build Configuration

The system automatically configures your build based on the JSON settings:

### Modular Architecture

The configuration system now supports modular architecture with feature modules:

```kotlin
// Feature modules are conditionally included based on configuration
dependencies {
    implementation(project(":compose"))
    implementation(project(":kotlin"))
    implementation(project(":shared"))
    implementation(project(":vonage-feature-chat")) // Conditionally included
}
```

### Product Flavors

Feature modules can define product flavors that are automatically selected:

```kotlin
// In vonage-feature-chat/build.gradle.kts
flavorDimensions += "chat"
productFlavors {
    create("enabled") {
        dimension = "chat"
    }
    create("disabled") {
        dimension = "chat"
    }
}
```

### Build Properties

Generated properties are used for build configuration:

```properties
# Generated in gradle/generated-config.properties
vonage.video.default_resolution=640x480
vonage.meetingRoom.allow_chat=true
vonage.audio.allow_advanced_noise_suppression=true
```

### Build Variants

You can have different configurations for different use cases by creating multiple JSON files:

- `config/app-config.json` (default configuration)
- `config/app-config-minimal.json` (lightweight version)
- `config/app-config-enterprise.json` (full features)

## Module Configuration

### Chat Feature Module

The chat feature demonstrates the modular configuration system:

```kotlin
// vonage-feature-chat/build.gradle.kts
flavorDimensions += "chat"
productFlavors {
    create("enabled") {
        dimension = "chat"
        // Chat feature implementation included
    }
    create("disabled") {
        dimension = "chat"
        // Empty implementation or stubs
    }
}
```

The main app automatically selects the correct flavor :

```kotlin
// app/build.gradle.kts
defaultConfig {
    val chatProperty = configProps.getProperty("vonage.meetingRoom.allow_chat", "false")
    missingDimensionStrategy("chat", chatProperty.toEnabledString())
}
```

## Switching Configurations

To switch between configurations :

1.* * Change the JSON file path * * in `app/build.gradle.kts`:
```kotlin
jsonConfig {
    configFile.set("config/app-config-minimal.json") // Changed from app-config.json
}
   ```

2. **Or modify the JSON values directly** in `config/app-config.json`:
   ```json
   {
     "meetingRoomSettings": {
       "allowChat": false,  // Disable chat feature
       "allowScreenShare": false
     }
   }
   ```

3. **Regenerate the configuration**:
   ```bash
   ./gradlew clean generateVonageConfig assembleDebug
   ```

4. **Verify the changes** in build output and generated files

## Generated Files

The plugin generates several files automatically:

1. **AppConfig.kt** - Structured Kotlin constants for runtime use
   ```
   app/build/generated/source/jsonConfig/com/vonage/android/config/AppConfig.kt
   ```

   Example structure:
   ```kotlin
   object AppConfig {
       object VideoSettings {
           const val ALLOW_BACKGROUND_EFFECTS: Boolean = true
           const val DEFAULT_RESOLUTION: String = "640x480"
       }
       object MeetingRoomSettings {
           const val ALLOW_CHAT: Boolean = true
           const val DEFAULT_LAYOUT_MODE: String = "active-speaker"
       }
   }
   ```

2. **BuildConfig.java** - Android BuildConfig with custom feature flags
   ```
   app/build/generated/source/buildConfig/debug/com/vonage/android/BuildConfig.java
   ```

   Example content:
   ```java
   public final class BuildConfig {
       public static final boolean FEATURE_CHAT_ENABLED = true;
       // ... other generated fields
   }
   ```

3. **Gradle Properties** - For build script configuration
   ```
   gradle/generated-config.properties
   ```

   Example content:
   ```properties
   vonage.video.default_resolution=640x480
   vonage.meetingRoom.allow_chat=true
   vonage.audio.allow_advanced_noise_suppression=true
   ```

## Best Practices

### When to use AppConfig vs BuildConfig

- **AppConfig**: Use for runtime configuration that might change based on server settings, user preferences, or feature toggles that don't affect build dependencies
- **BuildConfig**: Use for compile-time optimizations and feature flags that determine which modules or dependencies to include

### Modular Feature Development

```kotlin
// Feature interface in shared module
interface ChatFeature {
    fun isEnabled(): Boolean
    fun initializeChat()
     }

// Implementation in vonage-feature-chat enabled flavor
class ChatFeatureImpl : ChatFeature {
    override fun isEnabled() = AppConfig.MeetingRoomSettings.ALLOW_CHAT
    override fun initializeChat() {
        // Full chat implementation
    }
     }

// Stub in vonage-feature-chat disabled flavor
class ChatFeatureStub : ChatFeature {
    override fun isEnabled() = false
    override fun initializeChat() {
        // No-op implementation
    }
     }
```

### Performance Optimization with Product Flavors

```kotlin
class VideoManager {
    init {
        // Module selection happens at build time
        if (BuildConfig.FEATURE_CHAT_ENABLED) {
            // Chat module is included in APK
            chatFeature.initializeChat()
        }

        // Runtime configuration within enabled features
        if (AppConfig.VideoSettings.ALLOW_BACKGROUND_EFFECTS) {
            preloadBackgroundEffects()
        }
    }
     }
```

### Testing Different Configurations

```bash
# Test with chat disabled
# Method 1: Edit JSON directly
sed -i '' 's/"allowChat": true/"allowChat": false/' config/app-config.json
./gradlew clean assembleDebug

# Method 2: Use different config file
sed -i '' 's/app-config.json/app-config-minimal.json/' app/build.gradle.kts
./gradlew clean assembleDebug

# Method 3: Command line override
./gradlew assembleDebug -Dconfig.file=config/app-config-minimal.json
```

### Gradle Task Dependencies

The plugin automatically sets up task dependencies:

```kotlin
// In app/build.gradle.kts - automatically configured
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn("generateVonageConfig")
     }

tasks.matching { it.name.startsWith("ksp") }.configureEach {
    dependsOn("generateVonageConfig")
     }
```

## Configuration Properties Reference

| JSON Path | AppConfig | BuildConfig | Gradle Property | Description |
|-----------|-----------|-------------|-----------------|-------------|
| `videoSettings.allowBackgroundEffects` | `AppConfig.VideoSettings.ALLOW_BACKGROUND_EFFECTS` | - | `vonage.video.allow_background_effects` | Enable video background effects |
| `videoSettings.defaultResolution` | `AppConfig.VideoSettings.DEFAULT_RESOLUTION` | - | `vonage.video.default_resolution` | Default video resolution |
| `audioSettings.allowAdvancedNoiseSuppression` | `AppConfig.AudioSettings.ALLOW_ADVANCED_NOISE_SUPPRESSION` | - | `vonage.audio.allow_advanced_noise_suppression` | Advanced audio processing |
| `meetingRoomSettings.allowChat` | `AppConfig.MeetingRoomSettings.ALLOW_CHAT` | `BuildConfig.FEATURE_CHAT_ENABLED` | `vonage.meetingRoom.allow_chat` | Chat functionality & module selection |
| `meetingRoomSettings.allowScreenShare` | `AppConfig.MeetingRoomSettings.ALLOW_SCREEN_SHARE` | - | `vonage.meetingRoom.allow_screen_share` | Screen sharing capability |
| `meetingRoomSettings.allowCaptions` | `AppConfig.MeetingRoomSettings.ALLOW_CAPTIONS` | - | `vonage.meetingRoom.allow_captions` | Captions support |
| `meetingRoomSettings.defaultLayoutMode` | `AppConfig.MeetingRoomSettings.DEFAULT_LAYOUT_MODE` | - | `vonage.meetingRoom.default_layout_mode` | UI layout configuration |

## Command Line Usage

Override configuration at build time:

```bash
# Use different config file
./gradlew assembleDebug -Dconfig.file=/path/to/custom-config.json

# Check which flavors are selected
./gradlew app:dependencies --configuration debugRuntimeClasspath | grep vonage-feature

# View generated configuration
cat gradle/generated-config.properties
```

## Plugin Configuration

Minimal plugin setup in `app/build.gradle.kts`:

```kotlin
plugins {
    id("com.vonage.json-config")
     }

jsonConfig {
    configFile.set("config/app-config.json")
     }
```

The plugin automatically:
- Generates AppConfig.kt with structured constants
- Creates gradle properties for build configuration
- Sets up task dependencies
- Integrates with Android BuildConfig
- Supports product flavor selection

## Troubleshooting

### Common Issues

1. **Config not regenerating**
   ```bash
   # Force regeneration
   ./gradlew clean generateVonageConfig
   ```

2. **Product flavor selection errors**
   ```
   Error: Could not resolve all task dependencies for configuration ':app:debugRuntimeClasspath'.
   ```
   Solution: Ensure all feature modules have matching flavor dimensions

3. **Missing BuildConfig fields**
   - Check that `buildFeatures { buildConfig = true }` is enabled
   - Verify properties are loaded correctly: `if (configFile.exists())`

### Verification Commands

```bash
# Check generated AppConfig
cat app/build/generated/source/jsonConfig/com/vonage/android/config/AppConfig.kt

# Check BuildConfig fields
cat app/build/generated/source/buildConfig/debug/com/vonage/android/BuildConfig.java

# Check gradle properties
cat gradle/generated-config.properties

# Verify task execution
./gradlew generateVonageConfig --info
```

## Example: Adding a New Feature Module

1. **Create the module structure**:
   ```bash
   mkdir vonage-feature-newfeature
   ```

2. **Add product flavors** in `vonage-feature-newfeature/build.gradle.kts`:
   ```kotlin
   flavorDimensions += "newfeature"
   productFlavors {
       create("enabled") { dimension = "newfeature" }
       create("disabled") { dimension = "newfeature" }
   }
   ```

3. **Update JSON configuration**:
   ```json
   {
     "meetingRoomSettings": {
       "allowNewFeature": true
     }
   }
   ```

4. **Configure app build script**:
   ```kotlin
   defaultConfig {
       val newFeatureProperty = configProps.getProperty("vonage.meetingRoom.allow_new_feature", "false")
       missingDimensionStrategy("newfeature", newFeatureProperty.toEnabledString())
   }
   ```

5. **Test the configuration**:
   ```bash
   ./gradlew clean generateVonageConfig assembleDebug
   ```