package com.vonage.android.compose.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.opentok.android.BaseVideoRenderer

@Composable
fun VideoRenderer(
    renderer: BaseVideoRenderer,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { _ ->
            renderer.view
        },
        update = { _ ->
            renderer.view.refreshDrawableState()
        },
        modifier = modifier,
    )
}
