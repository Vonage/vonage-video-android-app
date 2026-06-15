# Code Style

## Static analysis

This project uses [Detekt](https://detekt.dev) for static analysis, including strict Compose lint rules configured in `build-tools/detekt/detekt.yml`.

```bash
# Run static analysis
./gradlew detekt

# Run all quality checks (includes tests, coverage, and static analysis)
./gradlew check
```

Detekt also runs automatically on `git push` via the pre-push hook. Install the hook once after cloning:

```bash
./gradlew installGitHooks
```

## Code formatting

[Spotless](https://github.com/diffplug/spotless) is used for code formatting. It is integrated into the `check` task and runs as part of the CI quality gate.

## CI quality gate

The following command mirrors what CI runs and should pass before opening a pull request:

```bash
./gradlew clean koverXmlReportDebug detekt
```

## Conventions

- **Kotlin + Java 17** throughout all modules (`sourceCompatibility`/`targetCompatibility` = `VERSION_17`).
- Branch from `develop`, not `main`. PRs target `develop`. Branch naming for internal contributors: `DEVELOPERNAME/TICKETNUMBER-SHORTDESCRIPTION`.
- Do not hardcode backend endpoints — use `BuildConfig.BASE_API_URL` or the generated `AppConfig`.
- SDK version is declared in `gradle/libs.versions.toml` (`opentokAndroidSdk`); do not pin it elsewhere.
