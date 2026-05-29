# Vonage Video Android App

A multi-module Android video calling application built on the Vonage Video SDK. The core call experience lives in the `vonage-meeting-room` library; the `app` module is the composition root.

## Language

**Participant**:
A single video stream in a call — either a remote subscriber or the local publisher. A participant whose `isScreenShare` is true represents a screen-sharing stream; that stream counts as a separate participant tile in the grid.
_Avoid_: User, subscriber, stream

**Publisher**:
The local participant controlled by the device's camera and microphone. There is exactly one publisher per call session.
_Avoid_: Local user, self

**Layout Mode**:
The user's active call view, selected via the bottom bar toggle. The two user-facing modes are Adaptive Grid and Speaker Layout.
_Avoid_: Layout type, view mode

**Adaptive Grid**:
A layout mode that arranges participant tiles in a dynamic grid. The number of columns and rows is determined by participant count and device orientation, with a maximum of 6 visible tiles. Any participants beyond the cap are represented by an Overflow Placeholder tile.
_Avoid_: Grid layout, tile grid

**Speaker Layout**:
A layout mode that gives one participant a large spotlight tile (70% of the screen) and arranges all others in a filmstrip. The spotlight participant is either the active speaker or a pinned participant.
_Avoid_: Active speaker layout, spotlight mode

**Active Speaker**:
The participant currently producing the loudest audio. In Speaker Layout, the active speaker automatically occupies the spotlight unless a participant is pinned.
_Avoid_: Current speaker, loudest participant

**Overflow Placeholder**:
A tile that occupies the last cell of the Adaptive Grid when participant count exceeds 6. It displays up to two avatar initials and a "+N" count for hidden participants.
_Avoid_: Extra participants tile, hidden participants indicator

**Grid Dimensions**:
The (columns × rows) configuration for the Adaptive Grid at a given moment, derived from participant count and device orientation.
_Avoid_: Grid size, grid shape

**Participant Tile**:
A single cell in the Adaptive Grid displaying one participant's video (or avatar when camera is off), name label, microphone indicator, and pin badge.
_Avoid_: Video card, participant card, video cell

## Relationships

- A **Call** contains one or more **Participants** (including the **Publisher**)
- A **Layout Mode** is selected per call session and persists until the user toggles it or a **Participant** is pinned
- Pinning a **Participant** always switches the **Layout Mode** to **Speaker Layout**
- The **Adaptive Grid** renders at most 6 **Participant Tiles**; beyond that, the last cell becomes the **Overflow Placeholder**
- **Grid Dimensions** are computed from participant count and orientation — screen share streams count as regular **Participants** toward the tile cap
- The **Active Speaker** drives the spotlight in **Speaker Layout** unless overridden by a pinned **Participant**

## Example dialogue

> **Dev:** "If someone starts screen sharing, does that push another participant into the Overflow Placeholder?"
> **Domain expert:** "Yes — a screen share is its own Participant Tile and counts toward the 6-tile cap, so if you already have 6 tiles visible and someone starts sharing their screen, the last tile becomes the Overflow Placeholder."

> **Dev:** "When the user rotates the device with 5 participants in the Adaptive Grid, what happens?"
> **Domain expert:** "The Grid Dimensions flip — portrait was 2 columns × 3 rows, so landscape becomes 3 columns × 2 rows. All 5 tiles are still visible, the empty cell just moves."

## Flagged ambiguities

- "Grid" was used loosely to mean both `CallLayoutType.GRID` (the old scrollable 2-column layout) and `CallLayoutType.ADAPTIVE_GRID` (the dynamic layout). Resolved: **Adaptive Grid** is the canonical term for the user-facing dynamic grid. `CallLayoutType.GRID` still exists in code but is not user-accessible.
