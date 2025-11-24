package com.vonage.android.chat.ui

import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.vonage.android.compose.components.ControlButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Chat2

@Composable
fun ChatBadgeButton(
    unreadCount: Int,
    onShowChat: () -> Unit,
    isChatShow: Boolean,
    modifier: Modifier = Modifier,
) {
    val badgeVisible = unreadCount > 0
    BadgedBox(
        modifier = modifier,
        badge = {
            if (badgeVisible) {
                Badge(
                    containerColor = VonageVideoTheme.colors.primary,
                    contentColor = Color.White,
                ) {
                    Text(
                        modifier = Modifier
                            .testTag("BOTTOM_BAR_CHAT_BADGE"),
                        text = "$unreadCount",
                    )
                }
            }
        },
    ) {
        ControlButton(
            modifier = Modifier
                .testTag("BOTTOM_BAR_CHAT_BUTTON"),
            onClick = onShowChat,
            icon = VividIcons.Solid.Chat2,
            isActive = isChatShow,
        )
    }
}

@Preview
@Composable
internal fun ChatBadgeButtonPreview() {
    VonageVideoTheme {
        ChatBadgeButton(
            unreadCount = 12,
            onShowChat = { },
            isChatShow = false,
        )
    }
}
