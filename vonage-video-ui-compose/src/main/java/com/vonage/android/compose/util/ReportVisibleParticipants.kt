package com.vonage.android.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.vonage.android.kotlin.model.CallFacade

/**
 * Reports [participantIds] as the on-screen participants for layouts that compose every tile they
 * show (no lazy viewport). The flow is registered once per [call] and follows later id changes.
 */
@Composable
internal fun ReportVisibleParticipants(call: CallFacade, participantIds: List<String>) {
    val currentIds = rememberUpdatedState(participantIds)
    SideEffect(call) {
        call.updateParticipantVisibilityFlow(snapshotFlow { currentIds.value })
    }
}
