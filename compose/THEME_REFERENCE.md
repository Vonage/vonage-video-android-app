# Vonage Theme Quick Reference

## Colors

### Primary Colors
```kotlin
VonageVideoTheme.colors.primary          // #9941FF (Cta500)
VonageVideoTheme.colors.onPrimary        // #FFFFFF (Canvas)
VonageVideoTheme.colors.primaryHover     // #871EFF (Cta600)
```

### Text Colors
```kotlin
VonageVideoTheme.colors.textPrimary      // #9941FF (Cta500)
VonageVideoTheme.colors.textSecondary    // #000000 (CanvasText)
VonageVideoTheme.colors.textTertiary     // #757575 (Accent500)
VonageVideoTheme.colors.textDisabled     // #B3B3B3 (Neutral300)
```

### Semantic Colors
```kotlin
// Error
VonageVideoTheme.colors.error            // #E61D1D
VonageVideoTheme.colors.onError          // #FFFFFF
VonageVideoTheme.colors.errorHover       // #CD0000

// Warning
VonageVideoTheme.colors.warning          // #BE5702
VonageVideoTheme.colors.onWarning        // #FFFFFF
VonageVideoTheme.colors.warningHover     // #A64C03

// Success
VonageVideoTheme.colors.success          // #1C8731
VonageVideoTheme.colors.onSuccess        // #FFFFFF
VonageVideoTheme.colors.successHover     // #1F7629
```

### Surface & Background
```kotlin
VonageVideoTheme.colors.background       // #F5F0FD (Cta50)
VonageVideoTheme.colors.onBackground     // #757575 (Neutral500)
VonageVideoTheme.colors.surface          // #FFFFFF (Canvas)
VonageVideoTheme.colors.onSurface        // #929292 (Neutral400)
VonageVideoTheme.colors.border           // #E6E6E6 (Neutral100)
VonageVideoTheme.colors.disabled         // #E6E6E6 (Neutral100)
```

## Typography

### Headlines & Titles
```kotlin
VonageVideoTheme.typography.headline          // 32sp, Medium, 40sp line
VonageVideoTheme.typography.subtitle          // 32sp, Medium, 44sp line
VonageVideoTheme.typography.heading1          // 28sp, Medium, 36sp line
VonageVideoTheme.typography.heading2          // 24sp, Medium, 32sp line
VonageVideoTheme.typography.heading3          // 20sp, Medium, 28sp line
VonageVideoTheme.typography.heading4          // 18sp, Medium, 24sp line
```

### Body Text
```kotlin
VonageVideoTheme.typography.bodyExtended          // 16sp, Normal, 24sp line
VonageVideoTheme.typography.bodyExtendedSemibold  // 16sp, SemiBold, 24sp line
VonageVideoTheme.typography.bodyBase              // 14sp, Normal, 20sp line
VonageVideoTheme.typography.bodyBaseSemibold      // 14sp, SemiBold, 20sp line
```

### Captions
```kotlin
VonageVideoTheme.typography.caption          // 12sp, Normal, 16sp line
VonageVideoTheme.typography.captionSemibold  // 12sp, SemiBold, 16sp line
```

## Shapes (Border Radius)

```kotlin
VonageVideoTheme.shapes.none          // 0dp
VonageVideoTheme.shapes.extraSmall    // 2dp
VonageVideoTheme.shapes.small         // 4dp
VonageVideoTheme.shapes.medium        // 8dp
VonageVideoTheme.shapes.large         // 12dp
VonageVideoTheme.shapes.extraLarge    // 24dp
```

## Dimensions

### Spacing
```kotlin
VonageVideoTheme.dimens.spaceNone       // 0dp
VonageVideoTheme.dimens.spaceXXSmall    // 2dp
VonageVideoTheme.dimens.spaceXSmall     // 4dp
VonageVideoTheme.dimens.spaceSmall      // 8dp
VonageVideoTheme.dimens.spaceMedium     // 12dp
VonageVideoTheme.dimens.spaceDefault    // 16dp
VonageVideoTheme.dimens.spaceLarge      // 24dp
VonageVideoTheme.dimens.spaceXLarge     // 32dp
VonageVideoTheme.dimens.spaceXXLarge    // 48dp
VonageVideoTheme.dimens.spaceXXXLarge   // 64dp
```

### Padding
```kotlin
VonageVideoTheme.dimens.paddingXSmall   // 4dp
VonageVideoTheme.dimens.paddingSmall    // 8dp
VonageVideoTheme.dimens.paddingMedium   // 12dp
VonageVideoTheme.dimens.paddingDefault  // 16dp
VonageVideoTheme.dimens.paddingLarge    // 24dp
VonageVideoTheme.dimens.paddingXLarge   // 32dp
```

### Component Sizes
```kotlin
// Buttons
VonageVideoTheme.dimens.buttonHeight        // 48dp
VonageVideoTheme.dimens.buttonHeightSmall   // 36dp
VonageVideoTheme.dimens.buttonHeightLarge   // 56dp

// Icons
VonageVideoTheme.dimens.iconSizeSmall       // 16dp
VonageVideoTheme.dimens.iconSizeDefault     // 24dp
VonageVideoTheme.dimens.iconSizeLarge       // 32dp
VonageVideoTheme.dimens.iconSizeXLarge      // 48dp

// Avatars
VonageVideoTheme.dimens.avatarSizeSmall     // 32dp
VonageVideoTheme.dimens.avatarSizeDefault   // 48dp
VonageVideoTheme.dimens.avatarSizeLarge     // 64dp
VonageVideoTheme.dimens.avatarSizeXLarge    // 96dp
```

### Borders & Elevation
```kotlin
// Border widths
VonageVideoTheme.dimens.borderWidthThin     // 1dp
VonageVideoTheme.dimens.borderWidthDefault  // 2dp
VonageVideoTheme.dimens.borderWidthThick    // 4dp

// Elevation
VonageVideoTheme.dimens.elevationNone       // 0dp
VonageVideoTheme.dimens.elevationSmall      // 2dp
VonageVideoTheme.dimens.elevationDefault    // 4dp
VonageVideoTheme.dimens.elevationMedium     // 8dp
VonageVideoTheme.dimens.elevationLarge      // 16dp
```

### Other
```kotlin
VonageVideoTheme.dimens.cardMinHeight       // 100dp
VonageVideoTheme.dimens.cardMaxWidth        // 600dp
VonageVideoTheme.dimens.dividerThickness    // 1dp
VonageVideoTheme.dimens.minTouchTarget      // 48dp
```

## Common Patterns

### Card with Theme
```kotlin
Card(
    shape = VonageVideoTheme.shapes.medium,
    colors = CardDefaults.cardColors(
        containerColor = VonageVideoTheme.colors.surface
    )
) {
    Text(
        text = "Content",
        style = VonageVideoTheme.typography.bodyBase,
        color = VonageVideoTheme.colors.textSecondary
    )
}
```

### Primary Button
```kotlin
Button(
    onClick = { },
    colors = ButtonDefaults.buttonColors(
        containerColor = VonageVideoTheme.colors.primary,
        contentColor = VonageVideoTheme.colors.onPrimary
    ),
    shape = VonageVideoTheme.shapes.small
) {
    Text("Button", style = VonageVideoTheme.typography.bodyBaseSemibold)
}
```

### Error Message
```kotlin
Text(
    text = "Error message",
    style = VonageVideoTheme.typography.bodyBase,
    color = VonageVideoTheme.colors.error
)
```

### Bordered Container
```kotlin
Box(
    modifier = Modifier
        .background(
            color = VonageVideoTheme.colors.surface,
            shape = VonageVideoTheme.shapes.medium
        )
        .border(
            width = VonageVideoTheme.dimens.borderWidthThin,
            color = VonageVideoTheme.colors.border,
            shape = VonageVideoTheme.shapes.medium
        )
        .padding(VonageVideoTheme.dimens.paddingDefault)
)
```

### Spaced Column
```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
    modifier = Modifier.padding(VonageVideoTheme.dimens.paddingDefault)
) {
    // Content
}
```

### Icon Button with Size
```kotlin
IconButton(
    onClick = { },
    modifier = Modifier.size(VonageVideoTheme.dimens.minTouchTarget)
) {
    Icon(
        imageVector = Icons.Default.Add,
        contentDescription = null,
        modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
    )
}
```
