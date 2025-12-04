package com.vonage.android.compose.components.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun ControlButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
    isActive: Boolean = false,
) {
    val iconColor = if (isActive) Color.White else Color.Gray
    val badgeVisible by remember(badgeCount) { derivedStateOf { badgeCount > 0 } }

    BadgedBox(
        modifier = modifier,
        badge = {
            if (badgeVisible) {
                Badge(
                    containerColor = Color.Gray,
                    contentColor = Color.White,
                ) {
                    Text(
                        modifier = Modifier,
                        //.testTag(BOTTOM_BAR_PARTICIPANTS_BADGE),
                        text = "$badgeCount",
                    )
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .size(VonageVideoTheme.dimens.minTouchTarget)
                .clip(CircleShape)
                .conditional(
                    isActive,
                    ifTrue = { background(Color.White.copy(alpha = 0.2f), CircleShape) },
                    ifFalse = { background(Color.Transparent, CircleShape) },
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
            )
        }
    }
}
