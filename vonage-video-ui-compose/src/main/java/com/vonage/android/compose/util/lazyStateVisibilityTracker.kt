package com.vonage.android.compose.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.snapshotFlow
import com.vonage.android.kotlin.model.CallFacade

@Composable
fun lazyStateVisibilityTracker(call: CallFacade, lazyState: LazyGridState): LazyGridState {
    val snapshotFlow = snapshotFlow {
        lazyState.layoutInfo.visibleItemsInfo.map { it.key as String }
    }
    DisposableEffect(call, lazyState) {
        call.updateParticipantVisibilityFlow(snapshotFlow)
        onDispose { }
    }
    return lazyState
}

@Composable
fun lazyStateVisibilityTracker(call: CallFacade, lazyState: LazyListState): LazyListState {
    val snapshotFlow = snapshotFlow {
        lazyState.layoutInfo.visibleItemsInfo.map { it.key as String }
    }
    DisposableEffect(call, lazyState) {
        call.updateParticipantVisibilityFlow(snapshotFlow)
        onDispose { }
    }
    return lazyState
}
