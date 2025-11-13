package com.vonage.android.compose.components

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.size
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.skydoves.compose.stability.runtime.TraceRecomposition
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant

@TraceRecomposition
@Composable
fun VideoRenderer2(
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
//        update = { container ->
//            val view = participant.view
//            val current = container.getChildAt(0)
//            if (current !== view) {
//                (view.parent as? ViewGroup)?.removeView(view)
//                if (current != null) container.removeViewAt(0)
//                val lp = FrameLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    ViewGroup.LayoutParams.MATCH_PARENT
//                )
//                container.addView(view, lp)
//            }
//        },
        modifier = modifier,
    )
}

@Composable
fun VideoRenderer(
    view: View,
    modifier: Modifier = Modifier,
) {
    // Keep container stable across recompositions
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
