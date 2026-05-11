package com.vonage.android.fx.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.kotlin.model.VideoEffect

@Suppress("UnusedParameter")
@Composable
fun VideoEffectIndicator(
    videoEffect: VideoEffect,
    onCameraBlur: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 0.dp,
    iconSize: Dp = 0.dp,
) {
    Spacer(modifier = modifier.size(size))
}
