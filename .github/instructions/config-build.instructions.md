---
applyTo: "{config/**/*.json,gradle/**/*.properties,**/*.gradle,**/*.gradle.kts,build-tools/**/*.kt}"
---

# Config and Build Instructions

- `config/app-config.json` is the source of truth for feature toggles and `baseApiUrl` placeholder values.
- Never manually edit `gradle/generated-config.properties`; regenerate with `./gradlew generateVonageConfig`.
- `app/build.gradle.kts` maps generated properties to `BuildConfig` flags and `missingDimensionStrategy(...)`; keep these mappings in sync with JSON keys.
- Keep plugin-driven generation intact:
  - `com.vonage.json-config` for `AppConfig` and generated Gradle properties.
  - `com.vonage.theme-generator` for Compose theme output from `config/theme.json`.
- When adding or renaming config keys, validate both runtime usage (`AppConfig`) and build-time usage (`BuildConfig`/flavors).
- Preserve task wiring that ensures generated config runs before Kotlin/KSP compilation in `app/build.gradle.kts`.

