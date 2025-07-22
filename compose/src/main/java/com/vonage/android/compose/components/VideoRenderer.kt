package com.vonage.android.compose.components

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun VideoRenderer(
    view: View,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { context ->
            FrameLayout(context)
        },
        update = { container ->
            container.removeAllViews()
            view.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
                container.addView(view)
            }
        },
        modifier = modifier,
    )
}
