package com.vonage.android.meetingroom.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.meetingroom.CallLayoutType
import com.vonage.android.meetingroom.MeetingRoomActions
import com.vonage.android.meetingroom.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.meetingroom.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MeetingRoomContent(
    call: CallFacade,
    actions: MeetingRoomActions,
    participants: ImmutableList<Participant>,
    layoutType: CallLayoutType,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        when (layoutType) {
            CallLayoutType.GRID -> ParticipantsLazyVerticalGridLayout(
                modifier = Modifier.fillMaxSize().testTag(MEETING_ROOM_PARTICIPANTS_GRID),
                participants = participants,
                call = call,
                actions = actions,
            )
            CallLayoutType.ADAPTIVE_GRID -> AdaptiveGrid(
                call = call,
                participants = participants,
                actions = actions,
                modifier = Modifier.fillMaxSize(),
            )
            CallLayoutType.SPEAKER_LAYOUT -> ActiveSpeakerLayout(
                call = call,
                participants = participants,
                actions = actions,
                modifier = Modifier.fillMaxSize().testTag(MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT),
            )
        }
    }
}

object MeetingRoomContentTestTags {
    const val MEETING_ROOM_PARTICIPANTS_GRID = "meeting_room_participants_grid"
    const val MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT = "meeting_room_participants_speaker_layout"
}
