package com.vonage.android.archiving.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.vonage.android.compose.anim.Pulsating
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Rec

@Composable
fun RecordingIndicator(
    modifier: Modifier = Modifier,
) {
    Pulsating {
        Icon(
            modifier = modifier,
            imageVector = VividIcons.Solid.Rec,
            tint = Color.Red,
            contentDescription = null,
        )
    }
}
