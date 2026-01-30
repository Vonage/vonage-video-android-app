# Vonage Reference App Config Plugin

An Android Studio / IntelliJ IDEA plugin that helps to work with configurable features in Vonage Reference App.

## Building the Plugin

```bash
./gradlew buildPlugin
```

The plugin will be built in `build/distributions/`.

## Running in Development

```bash
./gradlew runIde
```

This will launch a new instance of IntelliJ IDEA with your plugin installed.

## Installing

1. Build the plugin using `./gradlew buildPlugin`
2. In Android Studio/IntelliJ IDEA, go to Settings → Plugins → ⚙️ → Install Plugin from Disk
3. Select the ZIP file from `build/distributions/`

## Usage

Once installed, you can access the "Vonage" tool window from:
- View → Tool Windows → Vonage
- Or click the icon in the right sidebar

## TODO
- [ ] Auto detect vonage repository to auto-enable/disable
- [ ] Rebuild theme when theme file changes
- [ ] Use Kotlin UI DSL for the panel UI
