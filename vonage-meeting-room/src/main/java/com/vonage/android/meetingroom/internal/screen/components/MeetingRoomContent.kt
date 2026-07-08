package com.vonage.android.meetingroom.internal.screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.layout.AdaptiveGrid
import com.vonage.android.compose.layout.ActiveSpeakerLayout
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.meetingroom.internal.screen.CallLayoutType
import com.vonage.android.meetingroom.internal.screen.MeetingRoomActions
import com.vonage.android.meetingroom.internal.screen.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_GRID
import com.vonage.android.meetingroom.internal.screen.components.MeetingRoomContentTestTags.MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT
import com.vonage.android.meetingroom.internal.util.noOpCall
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MeetingRoomContent(
    call: CallFacade,
    actions: MeetingRoomActions,
    participants: ImmutableList<Participant>,
    layoutType: CallLayoutType,
    modifier: Modifier = Modifier,
) {
    val pinnedIds by call.pinnedParticipantIds.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize(),
    ) {
        when (layoutType) {
            CallLayoutType.ADAPTIVE_GRID -> {
                AdaptiveGrid(
                    call = call,
                    participants = participants,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_GRID),
                    participantContent = { participant, tileModifier ->
                        key("${participant.id}_grid") {
                            ParticipantVideoCard(
                                participant = participant,
                                actions = actions,
                                modifier = tileModifier,
                                isPinned = participant.id in pinnedIds,
                            )
                        }
                    },
                )
            }

            CallLayoutType.SPEAKER_LAYOUT -> {
                ActiveSpeakerLayout(
                    call = call,
                    participants = participants,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT),
                    participantContent = { participant, tileModifier ->
                        key("${participant.id}_speaker") {
                            ParticipantVideoCard(
                                participant = participant,
                                actions = actions,
                                modifier = tileModifier,
                                isPinned = participant.id in pinnedIds,
                            )
                        }
                    },
                )
            }
        }
    }
}

object MeetingRoomContentTestTags {
    const val MEETING_ROOM_PARTICIPANTS_GRID = "meeting-room-participants-grid"
    const val MEETING_ROOM_PARTICIPANTS_SPEAKER_LAYOUT = "meeting-room-participants-speaker-layout"
}

@PreviewLightDark
@Composable
internal fun MeetingRoomContentPreview() {
    VonageVideoTheme {
        MeetingRoomContent(
            call = noOpCall,
            actions = MeetingRoomActions(),
            participants = buildParticipants(25).toImmutableList(),
            layoutType = CallLayoutType.ADAPTIVE_GRID,
        )
    }
}
