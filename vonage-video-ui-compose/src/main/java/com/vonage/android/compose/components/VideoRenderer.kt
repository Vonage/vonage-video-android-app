package com.vonage.android.compose.components

import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.vonage.android.compose.preview.buildParticipants
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.kotlin.model.Participant

const val DELAY_MILLIS = 16L

@Composable
fun ParticipantVideoRenderer(
    participant: Participant,
    modifier: Modifier = Modifier,
) {
    val factory = remember { { context: Context -> FrameLayout(context) } }

    key(participant.id) {
        AndroidView(
            factory = factory,
            update = { container ->
                container.removeAllViews()
                participant.view.let { view ->
                    (view.parent as? ViewGroup)?.removeView(view)
                    
                    // Clear any cached minimum dimensions that might prevent proper sizing
                    view.minimumWidth = 0
                    view.minimumHeight = 0
                    
                    // Force the view to forget its previous measured dimensions
                    view.forceLayout()
                    
                    // Use MATCH_PARENT to tell the child it should fill the container
                    container.addView(
                        view,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                    
                    // Reinitialize the OpenTok renderer to fix layout issues when container size changes
                    participant.reinitializeRenderer()
                    
                    // Force immediate measurement and layout with multiple passes
                    container.post {
                        view.requestLayout()
                        container.requestLayout()
                        
                        // Double-check after a frame
                        container.postDelayed({
                            view.forceLayout()
                            container.requestLayout()
                        }, DELAY_MILLIS) // One frame delay
                    }
                }
            },
            onRelease = { container ->
                container.removeAllViews()
            },
            modifier = modifier,
        )
    }
}

@PreviewLightDark
@Composable
internal fun VideoRendererPreview() {
    VonageVideoTheme {
        ParticipantVideoRenderer(
            modifier = Modifier.size(480.dp, 340.dp),
            participant = buildParticipants(1).first(),
        )
    }
}
