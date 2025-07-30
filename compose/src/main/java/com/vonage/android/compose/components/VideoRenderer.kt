package com.vonage.android.compose.components

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme

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

@PreviewLightDark
@Composable
internal fun VideoRendererPreview() {
    VonageVideoTheme {
        VideoRenderer(
            modifier = Modifier.size(480.dp, 340.dp),
            view = previewCamera(),
        )
    }
}
