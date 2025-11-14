package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.screen.room.CallLayoutType
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun MeetingRoomContent(
    call: CallFacade,
    participants: List<Participant>,
    layoutType: CallLayoutType,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        when (layoutType) {
            CallLayoutType.SCOLL_GRID -> {
                SimpleGrid(
                    modifier = Modifier
                        .fillMaxSize(),
                    participants = participants.toImmutableList(),
                    call = call,
                )
            }
            CallLayoutType.GRID -> {
                AdaptiveGrid(
                    call = call,
                    participants = participants,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_GRID),
                )
            }

            CallLayoutType.SPEAKER_LAYOUT -> {
                AdaptiveSpeakerLayout(
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

//@OptIn(ExperimentalMaterial3Api::class)
//@PreviewLightDark
//@Composable
//internal fun MeetingRoomContentPreview() {
//    VonageVideoTheme {
//        MeetingRoomContent(
//            call = noOpCallFacade,
//            layoutType = CallLayoutType.GRID,
//        )
//    }
//}
