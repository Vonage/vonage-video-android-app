package com.vonage.android.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.AvatarInitialsTestTags.USER_INITIALS_ICON_TAG
import com.vonage.android.compose.components.AvatarInitialsTestTags.USER_INITIALS_TEXT_TAG
import com.vonage.android.compose.getParticipantColor
import com.vonage.android.compose.icons.PersonIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.shared.getInitials

@Composable
fun AvatarInitials(
    userName: String,
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    textStyle: TextStyle = VonageVideoTheme.typography.heading1,
) {
    val color = remember(userName) { userName.getParticipantColor() }
    val initials = remember(userName) { userName.getInitials() }

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
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
    const val USER_INITIALS_ICON_TAG = "user_initials_view_icon"
    const val USER_INITIALS_TEXT_TAG = "user_initials_view_text"
}

@PreviewLightDark
@Composable
internal fun AvatarInitialsWithNamePreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(VonageVideoTheme.dimens.paddingDefault)
        ) {
            AvatarInitials(userName = "Vonage User")
        }
    }
}

@PreviewLightDark
@Composable
internal fun AvatarInitialsWithoutNamePreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(VonageVideoTheme.dimens.paddingDefault)
        ) {
            AvatarInitials(userName = "")
        }
    }
}
