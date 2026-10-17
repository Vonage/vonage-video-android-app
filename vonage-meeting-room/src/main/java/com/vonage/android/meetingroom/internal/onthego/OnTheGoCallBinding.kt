package com.vonage.android.meetingroom.internal.onthego

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.Participant
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

private const val MIC_INDEX = 0
private const val CAMERA_INDEX = 1
private const val TALKING_OFFSET = 2

/**
 * Builds the controller once for this composition and keeps its call actions current.
 * Motion detection starts only when [enabled] is true.
 */
@Composable
internal fun rememberOnTheGoController(
    enabled: Boolean,
    actions: OnTheGoCallActions,
): OnTheGoController {
    val context = LocalContext.current
    val controller = remember(context, enabled) {
        OnTheGoController(
            configuration = OnTheGoConfiguration(isEnabled = enabled),
            preferences = SharedPreferencesOnTheGoPreferences(context),
            motionSource = if (enabled) SensorMotionActivitySource(context) else null,
            actions = actions,
        )
    }
    SideEffect { controller.actions = actions }
    DisposableEffect(controller) {
        onDispose { controller.dispose() }
    }
    return controller
}

/**
 * Pushes a connected snapshot while this composable is on screen.
 * Leaving the connected call sends the disconnected snapshot, which resets the mode.
 */
@Composable
internal fun BindOnTheGoCall(
    controller: OnTheGoController,
    call: CallFacade,
) {
    LaunchedEffect(controller, call) {
        try {
            observeOnTheGoSnapshot(call).collect { snapshot -> controller.update(snapshot) }
        } finally {
            controller.update(OnTheGoCallSnapshot.disconnected)
        }
    }
}

internal fun observeOnTheGoSnapshot(call: CallFacade) =
    combine(call.publisher, call.participantsStateFlow) { publisher, participants ->
        publisher to participants
    }.flatMapLatest { (publisher, participants) ->
        val micFlow = publisher?.isMicEnabled ?: flowOf(false)
        val cameraFlow = publisher?.isCameraEnabled ?: flowOf(false)
        val talkingFlows = participants.map { participant -> participant.isTalking }
        combine(listOf(micFlow, cameraFlow) + talkingFlows) { values ->
            onTheGoSnapshot(
                isConnected = true,
                isMicEnabled = values[MIC_INDEX],
                isCameraEnabled = values[CAMERA_INDEX],
                participants = participants.toFacts(values),
            )
        }
    }

private fun List<Participant>.toFacts(values: Array<Boolean>): List<OnTheGoParticipantFacts> {
    return mapIndexed { index, participant ->
        OnTheGoParticipantFacts(
            id = participant.id,
            name = participant.name,
            isPublisher = participant.isPublisher,
            isScreenShare = participant.isScreenShare,
            isTalking = values[index + TALKING_OFFSET],
        )
    }
}

/**
 * While On-the-Go is active, stop the meeting grid's visibility monitor and
 * subscribe only to the shared-content stream that is on screen.
 *
 * An empty visibility list is ignored by the call, and the active speaker is
 * forced visible by that monitor, so hiding the grid alone leaves incoming
 * camera video subscribed.
 */
@Composable
internal fun OnTheGoVideoSubscription(
    call: CallFacade,
    active: Boolean,
    visibleShareId: String?,
) {
    val participants by call.participantsStateFlow.collectAsStateWithLifecycle()
    LaunchedEffect(active, visibleShareId, participants) {
        if (!active) return@LaunchedEffect
        call.updateParticipantVisibilityFlow(emptyFlow())
        participants.filter { participant -> !participant.isPublisher }.forEach { participant ->
            participant.changeVisibility(
                subscribesOnTheGoVideo(
                    isScreenShare = participant.isScreenShare,
                    participantId = participant.id,
                    visibleShareId = visibleShareId,
                ),
            )
        }
    }
}

internal fun subscribesOnTheGoVideo(
    isScreenShare: Boolean,
    participantId: String,
    visibleShareId: String?,
): Boolean = isScreenShare && participantId == visibleShareId
