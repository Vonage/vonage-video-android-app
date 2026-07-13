package com.vonage.android.meetingroom.api

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * A custom button to be appended to the built-in bottom bar.
 *
 * Pass a [StateFlow] of this type to [MeetingRoomBuilder.additionalBottomBarActions] to inject
 * extra buttons after the built-in actions. The flow is collected with
 * `collectAsStateWithLifecycle`, so [isSelected] and [badgeCount] can be updated at runtime
 * (e.g. to reflect an unread-count badge on a custom panel button).
 *
 * Custom buttons participate in the same responsive overflow logic as built-in ones: if there
 * is not enough horizontal space to show all buttons inline, the overflow spills into the
 * "more" bottom sheet automatically.
 *
 * @param icon       Icon vector rendered inside the button.
 * @param label      Accessibility / overflow-sheet label.
 * @param isSelected When `true` the button renders in its active/highlighted state.
 * @param badgeCount Non-zero value shows a numeric badge on the button icon.
 * @param onClick    Invoked when the button is tapped.
 */
@ExperimentalMeetingRoomApi
data class MeetingRoomBottomBarAction(
    val icon: ImageVector,
    val label: String,
    val isSelected: Boolean = false,
    val badgeCount: Int = 0,
    val onClick: () -> Unit,
)
