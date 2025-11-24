package com.vonage.android.compose.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant

@Composable
fun ParticipantVideoRenderer(
    participant: Participant,
    modifier: Modifier = Modifier,
) {
    val factory = remember { { context: Context -> FrameLayout(context) } }

    AndroidView(
        factory = factory,
        update = { container ->
            container.removeAllViews()
            participant.view.let { view ->
                (view.parent as? ViewGroup)?.removeView(view)
                container.addView(view)
            }
        },
        modifier = modifier,
    )
}

// deprecated video renderer, only used in waiting room preview
@Composable
fun VideoRenderer(
    view: View,
    modifier: Modifier = Modifier,
) {
    val factory = remember { { context: Context -> FrameLayout(context) } }

    AndroidView(
        factory = factory,
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
