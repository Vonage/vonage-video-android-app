package com.vonage.android.screen.room.components.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.AvatarInitials
import com.vonage.android.screen.components.LinkifyText
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatRow(
    userName: String,
    message: String,
    date: Date,
    dateFormat: DateFormat,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        AvatarInitials(
            userName = userName,
            size = 36.dp,
            textStyle = VonageVideoTheme.typography.body,
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = userName,
                    style = VonageVideoTheme.typography.title,
                    color = VonageVideoTheme.colors.inverseSurface,
                )
                Text(
                    text = dateFormat.format(date),
                    style = VonageVideoTheme.typography.body,
                    color = VonageVideoTheme.colors.textPrimaryDisabled,
                )
            }
            LinkifyText(
                text = message,
            )
        }
    }
}

@PreviewLightDark
@Composable
internal fun ChatRowPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier.background(VonageVideoTheme.colors.surface)
        ) {
            ChatRow(
                userName = "Doctor Strange",
                message = "hi there!",
                date = Date(),
                dateFormat = SimpleDateFormat("hh:mm:ss", Locale.getDefault()),
            )
        }
    }
}
