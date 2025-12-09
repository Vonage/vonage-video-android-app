# Build Tools

Custom Gradle plugins and build configuration for the Vonage Video Android App.

## Contents

- **gradle-config-plugin/**: Custom Gradle plugins for JSON-to-code generation
- **detekt/**: Static code analysis configuration
- **detekt.gradle**: Detekt setup script
- **kover.gradle**: Code coverage configuration
- **sonar.gradle**: SonarQube integration

---

## Custom Gradle Plugins

### JSON Config Plugin

**Plugin ID**: `com.vonage.json-config`

Generates Kotlin configuration classes from `config/app-config.json`.

**Configuration**:
```kotlin
plugins {
    id("com.vonage.json-config")
}
```

**Input** (`config/app-config.json`):
```json
{
  "videoSettings": {
    "allowBackgroundEffects": true,
    "defaultResolution": "640x480"
  }
}
```

**Output** (Kotlin class):
```kotlin
object AppConfig {
    object VideoSettings {
        const val ALLOW_BACKGROUND_EFFECTS: Boolean = true
        const val DEFAULT_RESOLUTION: String = "640x480"
    }
}
```

**Usage**:
```kotlin
if (AppConfig.VideoSettings.ALLOW_BACKGROUND_EFFECTS) {
    enableBackgroundBlur()
}
```

The plugin also generates `gradle/generated-config.properties` for build-time configuration.

---

### Theme Generator Plugin

**Plugin ID**: `com.vonage.theme-generator`

Generates Jetpack Compose theme code from `config/theme.json`.

**Configuration**:
```kotlin
plugins {
    id("com.vonage.theme-generator")
}
```

**Running**:
```bash
./gradlew updateTheme
```

**Input** (`config/theme.json`):
```json
{
  "themes": {
    "vonage": {
      "colors": {
        "light": { "primary": "#0000FF", "background": "#FFFFFF" },
        "dark": { "primary": "#4444FF", "background": "#121212" }
      },
      "borderRadius": { "small": 8, "medium": 12, "large": 16 },
      "typography": {
        "mobile": {
          "headline": { "font-size": "32px", "font-weight": 700 }
        }
      }
    }
  }
}
```

**Generated Files**:
- `Color.kt`: Color schemes for light/dark themes
- `Typography.kt`: Text styles
- `Shape.kt`: Border radius definitions
- `Theme.kt`: Main theme composable

**Usage**:
```kotlin
@Composable
fun App() {
    VonageVideoTheme {
        Text("Hello", style = VonageVideoTheme.typography.headline)
    }
}
```

---

## Troubleshooting

**Plugin not found**: Ensure `settings.gradle.kts` includes:
```kotlin
pluginManagement {
    includeBuild("build-tools")
}
```

**Generated files not found**: Run `./gradlew clean build`
