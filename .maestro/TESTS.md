# Test Flows

## Active

| Flow | Description |
|---|---|
| `create-new-room.yaml` | Create a new room, enter username, and join the meeting |
| `join-with-camera-mic-allowed.yaml` | Join an existing room with camera and mic permissions granted |
| `goodbye-view-landing-page.yaml` | Join → end call → goodbye screen → return to landing |
| `waiting-room-controls-enabled.yaml` | Verify mic/camera enabled by default and persist in meeting |
| `waiting-room-controls-disabled.yaml` | Toggle mic/camera off in waiting room, verify disabled in meeting |
| `github-repo-link.yaml` | Verify GitHub repo button is visible on landing screen |
| `auth-required-create-join-room.yaml` | Signed-out user taps Create / Join while the backend answers 401 (simulated) → sign-in sheet instead of the waiting room. Needs `authSettings.allowAuthentication: true` (always on in the Maestro CI build) |
| `auth-login-logout.yaml` | Sign in from the top-bar button (fake Okta sign-in), check the account sheet shows the user, sign out, and check sign-in is offered again. Needs `allowAuthentication: true` |

## Disabled

Flows with `.yaml.disabled` extension are skipped by Maestro. Remove the suffix to re-enable.

| Flow | Reason |
|---|---|
| `goodbye-reenter-room.yaml.disabled` | Android re-enters directly to meeting room, not waiting room (differs from iOS flow) |
| `recording.yaml.disabled` | Android does not show a confirmation alert when starting recording (differs from iOS flow) |

## E2E launch arguments

Passed with `launchApp: arguments:` and read by `E2eTestFlags`. They are honoured only when E2E hooks are compiled in: always in debug builds, and in release only with `-Pvonage.e2eHooks=true` (used by the Maestro CI build). Store release builds ignore them.

| Argument | Effect |
|---|---|
| `e2eForceAuthRequired` | Signed-out `POST v2/createSession` requests get a local `401` (simulates an auth-enforcing backend) |
| `e2eFakeSignIn` | "Sign in with Okta" succeeds instantly as "E2E Test User" without opening the browser (the Okta page and redirect are not exercised) |
