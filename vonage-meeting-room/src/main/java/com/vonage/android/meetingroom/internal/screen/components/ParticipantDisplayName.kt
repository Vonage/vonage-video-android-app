package com.vonage.android.meetingroom.internal.screen.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.vonage.android.kotlin.Call
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.meetingroom.R

/**
 * Label to show for [participant] in the viewer's locale.
 *
 * Screen share streams carry only the sharer's name, so the "<name>'s screen" text is
 * built here on the receiving side. A sharer using English and a viewer using Japanese
 * each see the label in their own language.
 *
 * The local screen publisher is matched by id because its [Participant.isScreenShare]
 * is derived from a stream that does not exist yet when the publisher is created.
 */
@Composable
internal fun participantDisplayName(participant: Participant): String =
    if (participant.isScreenShare || participant.id == Call.PUBLISHER_SCREEN_ID) {
        stringResource(R.string.meeting_room_screen_share_participant_name, participant.name)
    } else {
        participant.name
    }
