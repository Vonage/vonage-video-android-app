package com.vonage.android.screen.room.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun TopBar(
    roomName: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = roomName,
            color = MaterialTheme.colorScheme.inverseSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        IconButton(onClick = { /* Handle copy room link */ }) {
            Icon(
                Icons.Default.Share,
                contentDescription = "Copy room link",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun TopBarPreview() {
    VonageVideoTheme {
        TopBar(
            roomName = "sample-name"
        )
    }
}
