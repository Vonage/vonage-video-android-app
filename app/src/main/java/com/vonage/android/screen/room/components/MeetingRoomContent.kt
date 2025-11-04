package com.vonage.android.screen.room.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.screen.room.CallLayoutType
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.screen.room.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT
import com.vonage.android.screen.room.noOpCallFacade
import kotlinx.collections.immutable.persistentListOf

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("LongParameterList")
@Composable
fun MeetingRoomContent(
    call: CallFacade,
    layoutType: CallLayoutType,
    modifier: Modifier = Modifier,
) {
    Log.d("XXX", "MeetingRoomContent recompose")
    val participants by call.participantsStateFlow.collectAsStateWithLifecycle(persistentListOf())

    Box(
        modifier = modifier
            .fillMaxSize(),
    ) {
        when (layoutType) {
            CallLayoutType.GRID -> {
                AdaptiveGrid(
                    participants = participants,
                    call = call,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_GRID),
                )
            }

            CallLayoutType.SPEAKER_LAYOUT -> {
                AdaptiveSpeakerLayout(
                    participants = participants,
                    call = call,
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

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
internal fun MeetingRoomContentPreview() {
    VonageVideoTheme {
        MeetingRoomContent(
            call = noOpCallFacade,
            layoutType = CallLayoutType.GRID,
        )
    }
}
