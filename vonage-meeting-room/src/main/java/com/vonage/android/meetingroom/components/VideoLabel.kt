package com.vonage.android.meetingroom.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
internal fun VideoLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = remember { Color.Black.copy(alpha = 0.6f) }

    Box(
        modifier = modifier
            .padding(
                top = VonageVideoTheme.dimens.paddingXSmall,
                bottom = VonageVideoTheme.dimens.paddingXSmall,
                start = VonageVideoTheme.dimens.paddingXSmall,
                end = 48.dp,
            )
            .background(backgroundColor, VonageVideoTheme.shapes.medium)
            .padding(
                horizontal = VonageVideoTheme.dimens.paddingSmall,
                vertical = VonageVideoTheme.dimens.paddingXSmall,
            )
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

