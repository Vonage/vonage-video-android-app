package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.CallLayoutType
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT
import com.vonage.android.screen.room.noOpCallFacade
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
fun MeetingRoomContent(
    call: CallFacade,
    participants: ImmutableList<Participant>,
    layoutType: CallLayoutType,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        when (layoutType) {
            CallLayoutType.GRID -> {
                ParticipantsLazyVerticalGridLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_GRID),
                    participants = participants,
                    call = call,
                )
            }

            CallLayoutType.ADAPTIVE_GRID -> {
                AdaptiveGrid(
                    call = call,
                    participants = participants,
                    modifier = Modifier
                        .fillMaxSize(),
                )
            }

            CallLayoutType.SPEAKER_LAYOUT -> {
                ActiveSpeakerLayout(
                    call = call,
                    participants = participants,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT),
                )
            }
        }
    }
}

object MeetingRoomContentTestTags {
    const val MEETING_ROOM_PARTICIPANTS_GRID = "meeting_room_participants_grid"
    const val MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT = "meeting_room_participants_speaker_layout"
}

@PreviewLightDark
@Composable
internal fun MeetingRoomContentPreview() {
    VonageVideoTheme {
        MeetingRoomContent(
            call = noOpCallFacade,
            participants = buildParticipants(25).toImmutableList(),
            layoutType = CallLayoutType.GRID,
        )
    }
}
