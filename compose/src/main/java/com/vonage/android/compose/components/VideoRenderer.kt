package com.vonage.android.compose.components

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoRenderer(
    view: View,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { _ ->
            view
        },
        update = { _ ->
            view.refreshDrawableState()
        },
        modifier = modifier,
    )
}
