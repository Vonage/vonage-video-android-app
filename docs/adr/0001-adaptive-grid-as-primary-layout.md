# Adaptive Grid replaces Grid as the primary user-facing layout mode

The `CallLayoutType` enum has always had two grid-like modes: `GRID` (a scrollable 2-column layout with no tile cap) and `ADAPTIVE_GRID` (a non-scrollable layout capped at 6 tiles with an overflow placeholder). Neither was truly dynamic — both hardcoded 2 columns and 3 rows regardless of how many participants were in the call.

We chose to evolve `ADAPTIVE_GRID` into the dynamic grid that iOS and web already ship — columns and rows adapt to participant count and orientation, up to a cap of 6 visible tiles — and to make it the default and only user-facing grid mode. `GRID` remains in the enum for backwards compatibility with any programmatic callers but is no longer reachable via the bottom bar toggle or the default initial state.

The alternative — adding a fourth `CallLayoutType` for the dynamic behaviour — was rejected because it would have added dead weight to the enum (two inert grid types) and required a migration of the default state in `MeetingRoomUiState`.
