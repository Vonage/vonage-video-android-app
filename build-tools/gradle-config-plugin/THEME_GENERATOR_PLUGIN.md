# Theme Generator Plugin

This Gradle plugin automatically generates or updates Material3 theme files for the Vonage Video Android app based on the configuration in `config/theme.json`.

## Features

- **Auto-generates theme files** from JSON configuration
- **Updates existing source files** directly (not in build folder)
- Generates Material3 `ColorScheme` for light and dark modes
- Creates custom `VonageColors`, `VonageTypography`, and `VonageShapes` data classes
- Supports selective generation (colors, typography, shapes, dimens)
- Maintains manual edits to dimension constants

## Setup

The plugin is already applied to the `compose` module. To use it in other modules:

```kotlin
plugins {
    id("com.vonage.theme-generator")
}

themeGenerator {
    themeJsonFile.set(file("../config/theme.json"))
    outputPackage.set("com.vonage.android.compose.theme")
    themeDirectory.set(file("src/main/java/com/vonage/android/compose/theme"))
    generateColors.set(true)       // Generate Color.kt
    generateTypography.set(true)   // Generate Typography.kt
    generateShapes.set(true)       // Generate Shape.kt
    generateDimens.set(false)      // Skip Dimen.kt (manually maintained)
}
```

## Configuration Options

### Required Properties

- `themeJsonFile`: Path to the theme.json configuration file
- `outputPackage`: Package name for generated files
- `themeDirectory`: Directory containing existing theme files to update

### Optional Properties (with defaults)

- `generateColors`: Generate Color.kt (default: `true`)
- `generateTypography`: Generate Typography.kt (default: `true`)
- `generateShapes`: Generate Shape.kt (default: `true`)
- `generateDimens`: Generate Dimen.kt (default: `false`)

## Usage

### Update Theme Files

Run the `updateTheme` task to regenerate theme files from `theme.json`:

```bash
./gradlew :compose:updateTheme
```

This will update the following files in the `compose/src/main/java/com/vonage/android/compose/theme` directory:
- `Color.kt` - Color constants and Material3 ColorSchemes
- `Typography.kt` - Text style definitions
- `Shape.kt` - Border radius shapes
- `Theme.kt` - Main theme composable function
- `Dimen.kt` - (only if `generateDimens` is true)

### How It Works

1. **Reads** `config/theme.json` and parses the theme configuration
2. **Validates** that the target directory exists
3. **Generates** Kotlin code for each enabled component
4. **Writes** directly to existing source files (e.g., `Color.kt`, not `GeneratedColor.kt`)
5. **Preserves** manual changes to files not regenerated (like `Dimen.kt` by default)

### Generated Files

#### Color.kt
- Light and dark color constants (e.g., `LightPrimary`, `DarkPrimary`)
- Material3 `LightColorScheme` and `DarkColorScheme`
- `VonageColors` data class with extended color properties
- `LocalVonageColors` CompositionLocal

#### Typography.kt
- Text style constants for all typography variants
- `VonageTypography` data class
- `LocalVonageTypography` CompositionLocal

#### Shape.kt
- Shape constants for border radius variants
- `VonageShapes` data class
- `LocalVonageShapes` CompositionLocal

#### Theme.kt
- `VonageVideoTheme` composable function
- Light/dark theme switching logic
- CompositionLocal providers
- `VonageVideoTheme` accessor object

#### Dimen.kt (optional)
- Dimension constants (spacing, padding, sizes, etc.)
- `VonageDimens` data class
- `LocalVonageDimens` CompositionLocal

## Theme Structure

The plugin expects `config/theme.json` to have this structure:

```json
{
  "themes": {
    "vonage": {
      "colors": {
        "light": { "primary": "#9941FF", ... },
        "dark": { "primary": "#B377FF", ... }
      },
      "typography": {
        "mobile": {
          "headline": { "fontSize": "32px", "fontWeight": 700, "lineHeight": "40px" },
          ...
        }
      },
      "borderRadius": {
        "none": 0,
        "extraSmall": 4,
        "small": 8,
        ...
      }
    }
  }
}
```

## Workflow

### Initial Setup
1. Create theme files manually or let the plugin generate them
2. Configure the plugin in `build.gradle.kts`
3. Run `./gradlew :compose:updateTheme`

### Making Changes
1. Edit `config/theme.json` with new colors, typography, or shapes
2. Run `./gradlew :compose:updateTheme` to update theme files
3. Theme files are automatically updated with new values
4. Manual customizations to `Dimen.kt` are preserved (since `generateDimens = false`)

### Custom Dimensions
If you need to regenerate dimensions:
1. Set `generateDimens.set(true)` in the plugin configuration
2. Run `./gradlew :compose:updateTheme`
3. Review and merge any manual changes you want to keep
4. Set `generateDimens.set(false)` again to prevent future overwrites

## Benefits

- **Single source of truth**: All theme values come from `theme.json`
- **Type-safe**: Generated Kotlin code is type-checked at compile time
- **Version controlled**: Generated files are in `src/` and committed to git
- **Selective generation**: Choose which files to regenerate
- **No build artifacts**: Updates existing source files, not intermediate build files

## Integration

The theme is used throughout the app via the `VonageVideoTheme` object:

```kotlin
@Composable
fun MyComposable() {
    VonageVideoTheme {
        Text(
            text = "Hello",
            color = VonageVideoTheme.colors.primary,
            style = VonageVideoTheme.typography.heading1
        )
    }
}
```

## Notes

- **Auto-generated comment**: Files include `// Auto-generated from theme.json` header
- **Manual edits**: Avoid manual edits to generated files (they'll be overwritten)
- **Dimens by default not regenerated**: Allows manual dimension adjustments
- **Theme.kt always generated**: Ensures proper integration of all components
