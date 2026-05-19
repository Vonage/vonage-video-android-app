---
applyTo: "**/*.{kt,kts}"
---

# Android Kotlin Instructions

- Respect module boundaries: keep orchestration in `app/`, SDK abstractions in `vonage-video-core/`, and reusable UI in `vonage-video-ui-compose/`.
- For optional features (`vonage-feature-*`), preserve the contract split:
  - `src/main` for interfaces/public API.
  - `src/enabled` and `src/disabled` for flavor implementations.
- If editing DI wiring, keep Hilt singleton graph patterns consistent with `app/src/main/java/com/vonage/android/di/`.
- If editing call/session behavior, trace impacts through `SessionRepository`, `MeetingRoomScreenViewModel`, and `VonageVideoClient`.
- Keep Compose code compatible with strict detekt compose rules (`build-tools/detekt/detekt.yml`).
- Prefer existing abstractions over new ad-hoc utilities (for example `CallFacade`, signal plugins, repositories).
- Do not hardcode backend endpoints when `BuildConfig.BASE_API_URL` or generated config is already used.
- Avoid introducing build-variant branching in runtime code when flavor source-set split already exists.

