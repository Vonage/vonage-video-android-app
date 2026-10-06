---
name: setup
description: First-time environment setup and environment doctor for the Vonage Video Android reference app (VERA). Use whenever the user wants to set up the project, onboard a new developer, bootstrap a machine, cannot sync or build in Android Studio, hits BASE_API_URL / generateVonageConfig / JDK toolchain / git-lfs / pre-push hook errors, asks "why won't this build", wants the Android Studio config plugin or the config TUI installed, or wants the environment checked after an Android Studio, AGP or JDK upgrade.
inclusion: manual
---

# VERA Android — Environment Setup & Doctor

Invoke with `/setup` in Claude Code, or `#vera-setup` in kiro.

Diagnose first, then fix only what is actually broken. Most "it won't build" reports on this repo come down to one of four things: no `BASE_API_URL`, no JDK the toolchain can use, git-lfs missing, or a stale generated config. Checking them takes a minute; blindly re-running the whole setup on a working machine wastes far more.

All commands run from the repo root.

## Step 1: Diagnose

```bash
# Toolchains. Everything compiles with JDK 17; the Android Studio plugin module needs a JDK 21.
# "Auto-download: Enabled" means Gradle can fetch a missing JDK itself.
./gradlew -q javaToolchains
```

```bash
# Backend URL — the one mandatory input. Either line must exist.
grep -E '^BASE_API_URL=' local.properties 2>/dev/null || echo "BASE_API_URL not in local.properties"
printenv BASE_API_URL || echo "BASE_API_URL not in env"
```

```bash
# Generated config (gitignored). Missing is fine on a fresh clone; a stale one explains "my flag change did nothing".
cat gradle/generated-config.properties 2>/dev/null | head -5
```

```bash
# git-lfs + hook. The pre-push hook exits 2 without git-lfs, and CI verifies snapshot goldens that are LFS-tracked.
git lfs version
test -x .git/hooks/pre-push && echo "pre-push hook installed" || echo "pre-push hook NOT installed"
head -c 60 "$(ls vonage-video-ui-compose/src/test/snapshots/images/*.png | head -1)"; echo
```

If that last command prints `version https://git-lfs...`, the goldens are LFS pointer files and every snapshot test will fail with nonsense diffs until `git lfs pull` runs.

## Step 2: Prerequisites

- **Android Studio** recent enough for AGP `9.3.1` (see `agp` in `gradle/libs.versions.toml`). The README's "Ladybug" minimum predates the AGP 9 upgrade and is too old.
- **JDK 17** for the project (`brew install --cask temurin@17`, or let the toolchain auto-provision). Gradle itself may run on a newer JDK; the toolchain picks 17 for compilation.
- **JDK 21** only for `:vonage-config-idea-plugin` (`jvmToolchain(21)`). Without it, root-level `./gradlew test` fails in that module while every other module is fine.
- **git-lfs**: `brew install git-lfs && git lfs install`, then `git lfs pull` if the clone predates the install.
- **A deployed backend** from [vonage-video-react-app](https://github.com/Vonage/vonage-video-react-app?tab=readme-ov-file#running-locally). The Android app has no backend of its own.

## Step 3: `BASE_API_URL`

The config generator reads `app-config.json`, substitutes `${BASE_API_URL}`, and **throws** when the value is not supplied by `local.properties` or the environment (`ConfigPropertiesGenerator.loadProps`). The environment wins when both are set.

```properties
# local.properties (gitignored)
BASE_API_URL=https://your-backend.example.com
```

Editing the placeholder in `app-config.json` by hand is **not** sufficient on its own — the generator still demands the key — so don't recommend that route even though `docs/GETTING_STARTED.md` lists it as option 3. Keep `app-config.json` on the placeholder so the file stays interchangeable with iOS/React.

Emulator against a backend on this machine: use `http://10.0.2.2:<port>`, not `localhost`. On a physical phone over USB, run `adb reverse tcp:<port> tcp:<port>` and use `http://localhost:<port>`. Leave off the trailing slash: the same string builds the Retrofit base URL, the deep link (`"$BASE_API_URL/room"`) and share links, which would get a double slash. The App Link host is derived from the URL too (`manifestPlaceholders["hostName"]`), so a bare IP is fine for local work but deep links won't verify.

**Plain `http://` backends need a cleartext exception.** Both `app/src/main/AndroidManifest.xml` and `app/src/debug/AndroidManifest.xml` set `android:usesCleartextTraffic="false"`. The build succeeds, then every API call fails at runtime with `CLEARTEXT communication to 10.0.2.2 not permitted by network security policy` in logcat, and the app looks like it cannot create a room. Either serve the backend over HTTPS (a tunnel works), or add a **debug-only** `app/src/debug/res/xml/network_security_config.xml` with a `<domain-config cleartextTrafficPermitted="true">` for `10.0.2.2` / `localhost` and reference it with `android:networkSecurityConfig="@xml/network_security_config"` in the debug manifest. Never relax it in `src/main`.

## Step 4: Generate, hook, build

```bash
./gradlew generateVonageConfig   # writes gradle/generated-config.properties + AppConfig.kt
./gradlew installGitHooks        # copies scripts/git-hooks/pre-push (runs git-lfs + detekt on push)
./gradlew installDebug           # or ▶️ in Android Studio
```

Gradle sync also regenerates the properties file eagerly at configuration time (`JsonConfigPlugin`), so in Android Studio a config edit followed by **Sync** is enough; a missing `BASE_API_URL` surfaces there as a warning and later as a hard failure of `:app:generateVonageConfig`.

Debug builds get `applicationId` `com.vonage.android.debug` and version suffix `-DEBUG`, so they coexist with a Play Store install.

## Step 5: Optional pieces

- **Okta sign-in** — off by default (`authSettings.allowAuthentication: false`). Turning it on needs `OKTA_ISSUER_URL`, `OKTA_CLIENT_ID`, `OKTA_SIGN_IN_REDIRECT_URI` (optional `OKTA_SCOPE`) in `local.properties` or env, and raises `minSdk` to 26. See `docs/AUTHENTICATION.md`.
- **Release builds** apply the Google Services + Crashlytics plugins (any task name containing "Release"), so `assembleRelease` needs `app/src/release/google-services.json`. CI writes a placeholder; locally, copy the real file or use a debug build. Release signing falls back to the committed `.sign/debug.keystore.jks` when `.sign/keystore.properties` is absent.
- **Maestro E2E** — `./scripts/install_maestro.sh` (needs Java 17 + Android SDK). Only needed for `./scripts/run_maestro_tests.sh`.

## Config editing tools (mention these to newcomers)

Both tools exist so nobody has to remember the regenerate step.

**Android Studio plugin** — module `vonage-config-idea-plugin`, plugin id `com.vonage.confighelper`, "Vonage Reference App Config".
```bash
./gradlew :vonage-config-idea-plugin:buildPlugin      # needs a JDK 21 toolchain
# → vonage-config-idea-plugin/build/distributions/*.zip
# Android Studio: Settings → Plugins → ⚙️ → Install Plugin from Disk…
./gradlew :vonage-config-idea-plugin:runIde           # sandbox IDE for plugin development
```
After install: **View → Tool Windows → Vonage** (right sidebar). The Config tab shows `app-config.json` as a tree and, when a change to that file is detected, runs `./gradlew clean generateVonageConfig` and streams the output into the panel (toggle in **Settings → Tools → Vonage Reference App Config**, `autoRunGradleTasks`). The Theme tab previews `theme.json` colours but does **not** regenerate the theme yet (open TODO in the module README) — run `./gradlew updateTheme` yourself.

**Config TUI** — `tools/config-tui-py`, a terminal form editor for both JSON files.
```bash
pipx install ./tools/config-tui-py && vonage-config     # or: python -m vonage_config_tui
```
It validates against the shared cross-platform JSON schemas (fetched from the React repo, cached for offline use), runs `generateVonageConfig` / `generateTheme` after save, and has a "Launch App" entry that runs `installDebug`.

**Sample host app** — `./gradlew :vonage-meeting-room-sample-app:installDebug` builds a minimal app that embeds `vonage-meeting-room` and takes the backend URL, room and name from a form. Handy for checking a backend before touching `local.properties`.

## Common failures

- **App builds but cannot create or join a room against a local backend** — cleartext HTTP is blocked; see Step 3. Check logcat for `CLEARTEXT communication … not permitted`.
- **`BASE_API_URL is not configured!`** — Step 3. Note that `:vonage-video-core:testDebugUnitTest` and other non-app modules still work without it (only `:app` depends on `generateVonageConfig`); `:app:*` tasks do not.
- **`Unrecognised top-level key(s) in the app config`** — someone added a new top-level group to `app-config.json`; the generator's key lists must be extended (`/feature-flag`, or `#vera-feature-flags` in kiro).
- **Pre-push rejected: "git-lfs was not found"** or detekt findings — install git-lfs, run `./gradlew detekt` and fix; don't `--no-verify` past it, CI runs the same check.
- **All snapshot tests fail after cloning** — LFS pointers, Step 1. Run `git lfs pull`.
- **`Could not find a Java installation ... languageVersion=21`** — building `:vonage-config-idea-plugin` without JDK 21; install one or skip the module (`-x :vonage-config-idea-plugin:test`).
- **`./gradlew test` takes forever** — it runs every flavor combination of `vonage-meeting-room` (16 variants). Use the per-variant tasks (`/run-tests`, or `#vera-testing` in kiro).
- **A flag flip did nothing** — `gradle/generated-config.properties` is stale or was hand-edited (it is overwritten on every sync). Re-run `generateVonageConfig` and sync.
- **Theme edit did nothing** — `generateVonageConfig` does *not* regenerate the theme. Run `./gradlew updateTheme` and commit the generated files under `vonage-video-ui-compose/src/main/java/com/vonage/android/compose/theme/`.

## Cautions

- Never commit `local.properties` (backend URL, Okta secrets, SDK path).
- `gradle/generated-config.properties` is generated and gitignored; `AppConfig.kt` lives under `app/build/`. The **theme** output, by contrast, is committed source — regenerate and commit it together with `theme.json` changes.
- The snapshot goldens are compared at a 1 % threshold to absorb macOS/Linux rendering differences; a diff above that is a real change, not a platform artefact.
