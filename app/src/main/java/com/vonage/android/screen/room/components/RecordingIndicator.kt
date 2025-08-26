package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun RecordingIndicator() {
    Icon(
        modifier = Modifier
            .padding(end = 4.dp),
        imageVector = Icons.Default.FiberManualRecord,
        tint = Color.Red,
        contentDescription = null,
    )
}
