# Feature Toggle Change Prompt

Use this prompt when changing an optional feature module (`vonage-feature-*`) that supports `enabled`/`disabled` flavors.

## Task
Implement the requested feature change while preserving the repository's flavor-based feature-toggle pattern.

## Required Checklist
- Identify the feature contract in `src/main` and update it only if needed.
- Apply matching behavior changes in both `src/enabled` and `src/disabled`.
- Verify app-level dimension mapping in `app/build.gradle.kts` (`missingDimensionStrategy(...)`, `BuildConfig.FEATURE_*`).
- Confirm related DI bindings remain valid for both flavors.
- Confirm config key mapping from `config/app-config.json` to generated properties and runtime/build usage.

## Output Format
1. Files changed and why.
2. Contract changes in `src/main`.
3. Enabled variant implementation changes.
4. Disabled variant implementation changes.
5. Build/config mapping changes.
6. Verification commands run.

## Verification Commands
```bash
./gradlew generateVonageConfig
./gradlew :app:assembleDebug
./gradlew test
./gradlew detekt
```

