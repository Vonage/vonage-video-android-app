# Vonage Custom Material Theme

This custom Material3 theme is based on the Vonage design system defined in `config/theme.json` and `config/tokens.json`.

## Overview

The theme includes:
- **Colors**: Complete color palette with primary, secondary, tertiary, error, warning, and success colors
- **Typography**: Full typography scale from headline to caption with normal and semibold variants
- **Shapes**: Border radius values from none (0dp) to extra-large (24dp)

## Usage

### Basic Usage

Wrap your composable content with `VonageVideoTheme`:

```kotlin
@Composable
fun MyApp() {
    VonageVideoTheme {
        // Your app content here
        MyScreen()
    }
}
```

### Accessing Theme Properties

#### Colors

```kotlin
@Composable
fun MyComponent() {
    // Access custom Vonage colors
    val colors = VonageVideoTheme.colors
    
    Box(
        modifier = Modifier
            .background(colors.primary)
            .border(1.dp, colors.border)
    ) {
        Text(
            text = "Hello",
            color = colors.onPrimary
        )
    }
}
```

Available color properties:
- `primary`, `onPrimary`, `primaryHover`
- `secondary`, `onSecondary`
- `tertiary`, `onTertiary`
- `accent`, `onAccent`
- `background`, `onBackground`
- `surface`, `onSurface`
- `error`, `onError`, `errorHover`
- `warning`, `onWarning`, `warningHover`
- `success`, `onSuccess`, `successHover`
- `border`, `disabled`, `textDisabled`
- `textPrimary`, `textSecondary`, `textTertiary`

#### Typography

```kotlin
@Composable
fun MyComponent() {
    val typography = VonageVideoTheme.typography
    
    Column {
        Text(
            text = "Headline",
            style = typography.headline
        )
        Text(
            text = "Body text",
            style = typography.bodyBase
        )
        Text(
            text = "Caption",
            style = typography.caption
        )
    }
}
```

Available typography styles:
- `headline` - 32sp, Medium (500)
- `subtitle` - 32sp, Medium (500)
- `heading1` - 28sp, Medium (500)
- `heading2` - 24sp, Medium (500)
- `heading3` - 20sp, Medium (500)
- `heading4` - 18sp, Medium (500)
- `bodyExtended` - 16sp, Normal (400)
- `bodyExtendedSemibold` - 16sp, SemiBold (600)
- `bodyBase` - 14sp, Normal (400)
- `bodyBaseSemibold` - 14sp, SemiBold (600)
- `caption` - 12sp, Normal (400)
- `captionSemibold` - 12sp, SemiBold (600)

#### Shapes

```kotlin
@Composable
fun MyComponent() {
    val shapes = VonageVideoTheme.shapes
    
    Card(
        shape = shapes.medium, // 8dp corner radius
        modifier = Modifier.fillMaxWidth()
    ) {
        // Card content
    }
}
```

Available shape properties:
- `none` - 0dp
- `extraSmall` - 2dp
- `small` - 4dp
- `medium` - 8dp
- `large` - 12dp
- `extraLarge` - 24dp

## Color Reference

### Token Colors (from tokens.json)

| Token | Hex | Usage |
|-------|-----|-------|
| Cta500 | #9941FF | Primary CTA color |
| Cta600 | #871EFF | Primary hover state |
| Alert500 | #E61D1D | Error states |
| Warning500 | #BE5702 | Warning states |
| Success500 | #1C8731 | Success states |
| Neutral100 | #E6E6E6 | Borders, disabled backgrounds |
| Neutral300 | #B3B3B3 | Disabled text |
| Canvas | #FFFFFF | White background |
| CanvasText | #000000 | Black text |

## Material 3 Integration

The theme also configures Material 3's ColorScheme, so standard Material 3 components will automatically use the Vonage color palette:

```kotlin
Button(
    onClick = { /* ... */ }
) {
    Text("Click me") // Uses Material theme colors
}
```

## Dark Theme Support

The theme includes basic dark theme support. Enable it with:

```kotlin
VonageVideoTheme(darkTheme = true) {
    // Your content
}
```

Or let it follow system theme:

```kotlin
VonageVideoTheme(darkTheme = isSystemInDarkTheme()) {
    // Your content
}
```

## Notes

- Font family is currently set to `FontFamily.Default`. Replace with Inter font family when available.
- Typography values are based on mobile specifications from theme.json. Consider creating responsive typography for desktop/tablet.
- All design tokens are sourced from `config/tokens.json` and `config/theme.json`.
