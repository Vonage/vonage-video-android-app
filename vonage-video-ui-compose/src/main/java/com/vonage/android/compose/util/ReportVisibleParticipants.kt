package com.vonage.android.compose.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import com.vonage.android.kotlin.model.CallFacade

@Composable
internal fun ReportVisibleParticipants(call: CallFacade, participantIds: List<String>) {
    val currentIds = rememberUpdatedState(participantIds)
    SideEffect(call) {
        call.updateParticipantVisibilityFlow(snapshotFlow { currentIds.value })
    }
}
