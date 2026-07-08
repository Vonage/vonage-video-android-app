package com.vonage.android.compose.util

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.snapshotFlow
import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.Flow

@Composable
fun <T : ScrollableState> lazyStateVisibilityTracker(call: CallFacade, lazyState: T): T {
    val snapshotFlow: Flow<List<String>> = when (lazyState) {
        is LazyGridState -> {
            snapshotFlow {
                lazyState.layoutInfo.visibleItemsInfo.map { it.key as String }
            }
        }

        is LazyListState ->
            snapshotFlow {
                lazyState.layoutInfo.visibleItemsInfo.map { it.key as String }
            }

        else -> throw UnsupportedOperationException("Wrong initial state.")
    }
    DisposableEffect(call, lazyState) {
        call.updateParticipantVisibilityFlow(snapshotFlow)
        onDispose { }
    }
    return lazyState
}
