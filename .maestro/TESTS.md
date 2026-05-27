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

## Disabled

Flows with `.yaml.disabled` extension are skipped by Maestro. Remove the suffix to re-enable.

| Flow | Reason |
|---|---|
| `goodbye-reenter-room.yaml.disabled` | Android re-enters directly to meeting room, not waiting room (differs from iOS flow) |
| `recording.yaml.disabled` | Android does not show a confirmation alert when starting recording (differs from iOS flow) |
