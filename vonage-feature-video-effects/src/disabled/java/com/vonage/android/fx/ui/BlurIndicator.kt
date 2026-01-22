package com.vonage.android.fx.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vonage.android.kotlin.model.BlurLevel

@Suppress("UnusedParameter")
@Composable
fun BlurIndicator(
    isCameraEnabled: Boolean,
    blurLevel: BlurLevel,
    onCameraBlur: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Spacer(modifier = Modifier.size(56.dp))
}
