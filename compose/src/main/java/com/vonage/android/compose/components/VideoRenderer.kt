package com.vonage.android.compose.components

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.compose.util.VideoViewLifecycleManager
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.compose.theme.VonageVideoTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun VideoRenderer(
    view: StateFlow<View>,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val view by view.collectAsStateWithLifecycle()
    
    val factory = remember {
        { context: Context -> FrameLayout(context) }
    }
    
    DisposableEffect(view, lifecycleOwner) {
        val observer = VideoViewLifecycleManager.createLifecycleObserver(view)
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            VideoViewLifecycleManager.detachVideoView(view)
        }
    }
    
    AndroidView(
        factory = factory,
        update = { container ->
            if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                VideoViewLifecycleManager.attachVideoView(container, view, lifecycleOwner)
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
            view = MutableStateFlow(previewCamera()),
        )
    }
}
