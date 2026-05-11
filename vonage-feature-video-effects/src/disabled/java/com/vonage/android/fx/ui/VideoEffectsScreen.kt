package com.vonage.android.fx.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.kotlin.model.VideoEffect
import kotlinx.collections.immutable.ImmutableList

@Suppress("UnusedParameter", "EmptyFunctionBlock")
@Composable
fun VideoEffectsScreen(
    backgrounds: ImmutableList<VideoBackgroundItem>,
    selectedEffect: VideoEffect,
    onEffectSelect: (VideoEffect) -> Unit,
    modifier: Modifier = Modifier,
) {
}
