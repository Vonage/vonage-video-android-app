package com.vonage.android.screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.icons.PersonIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.components.AvatarInitialsTestTags.USER_INITIALS_CIRCLE_TAG
import com.vonage.android.screen.components.AvatarInitialsTestTags.USER_INITIALS_ICON_TAG
import com.vonage.android.screen.components.AvatarInitialsTestTags.USER_INITIALS_TEXT_TAG
import com.vonage.android.util.getInitials
import com.vonage.android.util.getParticipantColor

@Composable
fun AvatarInitials(
    userName: String,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    textStyle: TextStyle = MaterialTheme.typography.displayMedium,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        val color = remember(userName) { userName.getParticipantColor() }
        val initials = remember(userName) { userName.getInitials() }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag(USER_INITIALS_CIRCLE_TAG),
        ) {
            drawCircle(SolidColor(color))
        }

        if (initials.isEmpty()) {
            PersonIcon(
                modifier = Modifier.testTag(USER_INITIALS_ICON_TAG),
                size = 56.dp,
                tint = Color.White,
            )
        } else {
            Text(
                modifier = Modifier.testTag(USER_INITIALS_TEXT_TAG),
                text = initials,
                style = textStyle,
                color = Color.White,
            )
        }
    }
}

object AvatarInitialsTestTags {
    const val USER_INITIALS_CIRCLE_TAG = "user_initials_view_circle"
    const val USER_INITIALS_ICON_TAG = "user_initials_view_icon"
    const val USER_INITIALS_TEXT_TAG = "user_initials_view_text"
}

@Preview
@Composable
internal fun AvatarInitialsPreview() {
    VonageVideoTheme {
        AvatarInitials(
            userName = "Vonage User",
        )
    }
}

@Preview
@Composable
internal fun AvatarInitialsEmptyPreview() {
    VonageVideoTheme {
        AvatarInitials(
            userName = "",
        )
    }
}
