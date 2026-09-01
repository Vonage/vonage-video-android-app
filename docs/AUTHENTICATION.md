# Authentication

The Vonage Video Android Reference App supports optional authentication via an external identity provider (IdP). The current implementation integrates [Okta](https://developer.okta.com/) using the [okta-mobile-kotlin](https://github.com/okta/okta-mobile-kotlin) SDK for browser-based OIDC sign-in.

Authentication lives in its own optional feature module, `vonage-feature-okta`, following the repository's standard `enabled`/`disabled` flavor pattern. It is **disabled by default**: until the backend middleware enforces authentication, users can keep using the app exactly as before — requests are simply sent without an `Authorization` header when no token is available.

## Enabling / Disabling

The feature is controlled by `config/app-config.json`:

```json
{
  "authSettings": {
    "allowAuthentication": true
  }
}
```

| Key | Effect |
|---|---|
| `authSettings.allowAuthentication` | `true` selects the `enabled` flavor of `vonage-feature-okta`, compiles in the Okta SDK, and shows the sign-in entry point on the landing screen. `false` (default) selects the inert `disabled` flavor — no Okta code is shipped and the UI is unchanged. |

After changing the value, regenerate the build configuration:

```bash
./gradlew generateVonageConfig
```

This maps the flag to `BuildConfig.FEATURE_AUTHENTICATION_ENABLED` and the `okta` flavor dimension (`missingDimensionStrategy` in `app/build.gradle.kts`), like every other optional feature.

> **Note:** the Okta SDK requires API 26+. When `allowAuthentication` is `true`, the app's `minSdk` is raised from 24 to 26 (Android 8.0) automatically.

## Okta configuration

The OIDC client credentials are **never committed to the repository**. They are read at build time from `local.properties` (local development) or environment variables (CI/CD — environment wins when both are set):

```properties
# local.properties
OKTA_ISSUER_URL=https://your-org.okta.com
OKTA_CLIENT_ID=your_okta_client_id
OKTA_SIGN_IN_REDIRECT_URI=com.vonage.android:/callback
# Optional, defaults to "openid profile offline_access"
OKTA_SCOPE=openid profile offline_access
```

| Key | Description |
|---|---|
| `OKTA_ISSUER_URL` | Your Okta authorization server URL |
| `OKTA_CLIENT_ID` | The OIDC client ID registered in your Okta application |
| `OKTA_SIGN_IN_REDIRECT_URI` | The URI Okta redirects to after authentication. Its scheme (the part before `:`) is registered as the `webAuthenticationRedirectScheme` manifest placeholder so the SDK's redirect activity can capture the callback. Register the same URI in your Okta application. |
| `OKTA_SCOPE` | OIDC scopes (default: `openid profile offline_access`) |

The values reach the app as `BuildConfig` fields and are assembled into an `OktaConfig` in `app/src/main/java/com/vonage/android/di/AuthModule.kt`.

For CI, set the same names as environment variables (e.g. GitHub Actions secrets):

```yaml
env:
  OKTA_ISSUER_URL: ${{ secrets.OKTA_ISSUER_URL }}
  OKTA_CLIENT_ID: ${{ secrets.OKTA_CLIENT_ID }}
  OKTA_SIGN_IN_REDIRECT_URI: ${{ secrets.OKTA_SIGN_IN_REDIRECT_URI }}
```

## Architecture

### Module layout

```
vonage-feature-okta/
├── src/main/                      # Public contract — no Okta SDK imports
│   ├── VonageOktaAuth.kt          #   isCapable, authState, signIn/signOut/currentToken
│   ├── AuthState.kt               #   NotAuthenticated / Authenticated(AuthenticatedUser)
│   ├── IdProvider.kt              #   Identity provider descriptor (id + display name)
│   ├── OktaConfig.kt              #   OIDC client configuration value type
│   └── ui/AuthTestTags.kt         #   Test tags (aligned with iOS accessibility ids)
├── src/enabled/                   # Real implementation (Okta SDK compiled in)
│   ├── EnabledVonageOktaAuth.kt   #   State machine over sign-in/sign-out results
│   ├── data/
│   │   ├── BrowserSignInProvider.kt      # SDK abstraction for testability
│   │   ├── OktaBrowserSignInProvider.kt  # okta-mobile-kotlin wrapper
│   │   └── IdTokenDecoder.kt             # Reads the `name` claim for the UI
│   ├── di/OktaModule.kt           #   provideVonageOktaAuth(context, config)
│   └── ui/SignInButton.kt         #   Top-bar button + sign-in / account sheets
├── src/disabled/                  # Inert stubs with identical signatures
│   ├── DisabledVonageOktaAuth.kt
│   ├── di/OktaModule.kt
│   └── ui/SignInButton.kt         #   Renders nothing
└── src/testEnabled/               # JUnit 5 unit tests
```

Only `src/enabled` references the Okta SDK (via `enabledImplementation` in the module's `build.gradle.kts`), so a disabled build ships without any Okta code.

### Token injection

Backend requests attach the access token through two interceptors, mirroring the decorator approach used by the iOS reference app:

- **App-level Retrofit** (feedback endpoint, etc.): `AuthorizationInterceptor` in `app/src/main/java/com/vonage/android/data/network/interceptor/` is registered in `RetrofitModule.provideHttpClient()`.
- **Meeting-room SDK Retrofit** (session fetch, archiving, captions): `MeetingRoomBuilder.authTokenProvider { ... }` accepts an optional `MeetingRoomAuthTokenProvider`; the app supplies one backed by `VonageOktaAuth` in `AppNavHost`. `vonage-meeting-room` stays Hilt-free and Okta-free — it only sees a `() -> String?`-shaped provider.

In both places the same rule applies: **no token → no header**. Sign-in state never blocks a request, so the app remains fully usable against a backend that does not (yet) enforce authentication. Token refresh happens transparently inside the provider when the access token has expired.

### UI integration

The landing screen's top bar renders `SignInButton` from the feature module:

- Not authenticated → person icon opens a bottom sheet listing the available identity providers (currently Okta) with a "Sign in with Okta" button that launches the browser (Chrome Custom Tab) flow.
- Authenticated → highlighted icon opens an account sheet showing the user's name and a sign-out button.

The disabled flavor's `SignInButton` has an identical signature and renders nothing, so no feature-flag branching exists in `app/` UI code. All other screens are untouched.

### Session persistence & security

- Tokens are stored by the Okta SDK in an **encrypted Room database** on device; the session is restored on app start (`restoreSession()`), so users stay signed in across restarts.
- Token storage files are excluded from Android Auto Backup and device-to-device transfer (`app/src/main/res/xml/backup_rules.xml` and `data_extraction_rules.xml`).
- Sign-out removes the stored credential.

## Extending with additional providers

The contract in `src/main` is provider-agnostic (`VonageOktaAuth`, `AuthState`, `IdProvider`). To add another identity provider, create a sibling feature module implementing the same contract, register its `IdProvider` in the sign-in sheet, and provide it from `AuthModule` — no changes to the token-injection plumbing are required.

## Testing

```bash
# Feature module unit tests (enabled flavor)
./gradlew :vonage-feature-okta:testEnabledDebugUnitTest

# App-level interceptor tests
./gradlew :app:testDebugUnitTest --tests "com.vonage.android.data.network.interceptor.*"
```

Compose test tags on the auth UI (`auth-button`, `auth-sign-in-screen`, `auth-sign-in-provider-okta`, `auth-account-menu`, `auth-sign-out-button`, …) are aligned with the iOS accessibility identifiers so Maestro E2E flows can be shared across platforms.
