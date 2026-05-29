package com.vonage.android.meetingroom.internal.util

/** Maximum number of participant tiles rendered simultaneously in the Adaptive Grid. */
internal const val MAX_GRID_TILES = 6

/** Maximum number of cells in the Speaker Layout filmstrip (real tiles + placeholder). */
internal const val MAX_FILMSTRIP_TILES = 2

private const val QUAD_PARTICIPANT_THRESHOLD = 4

/**
 * Returns the [GridDimensions] for the Adaptive Grid based on participant count and orientation.
 *
 * Portrait breakpoints:
 *   1       → 1×1
 *   2       → 1×2
 *   3–4     → 2×2
 *   5+      → 2×3
 *
 * Landscape mirrors portrait by flipping columns and rows:
 *   1       → 1×1
 *   2       → 2×1
 *   3–4     → 2×2
 *   5+      → 3×2
 *
 * The [MAX_GRID_TILES] cap and overflow placeholder are enforced by the composable, not here.
 */
internal fun gridLayoutFor(participantCount: Int, isLandscape: Boolean): GridDimensions {
    val portrait = when {
        participantCount <= 1 -> GridDimensions(columns = 1, rows = 1)
        participantCount <= 2 -> GridDimensions(columns = 1, rows = 2)
        participantCount <= QUAD_PARTICIPANT_THRESHOLD -> GridDimensions(columns = 2, rows = 2)
        else -> GridDimensions(columns = 2, rows = 3)
    }
    return if (isLandscape) GridDimensions(columns = portrait.rows, rows = portrait.columns)
    else portrait
}
