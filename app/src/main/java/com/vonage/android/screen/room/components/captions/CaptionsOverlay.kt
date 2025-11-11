package com.vonage.android.screen.room.components.captions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.StateFlow

private const val OVERLAY_ZINDEX = 10F

@Composable
fun CaptionsOverlay(
    call: CallFacade,
    modifier: Modifier = Modifier,
) {
    val captions by call.captionsStateFlow.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .zIndex(OVERLAY_ZINDEX)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        captions?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(4.dp)
                    .background(
                        Color.Black.copy(alpha = 0.6f),
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = it,
                    color = Color.White,
                    fontSize = 14.sp,
                    overflow = TextOverflow.StartEllipsis,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
