package com.vonage.android.compose.components.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.modifier.conditional
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Blur

@Composable
fun ActionCell(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClickCell: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
) {
    val defaultColor = VonageVideoTheme.colors.secondary
    val badgeVisible by remember(badgeCount) { derivedStateOf { badgeCount > 0 } }

    Column(
        modifier = modifier
            .height(VonageVideoTheme.dimens.avatarSizeXLarge)
            .clickable(onClick = onClickCell)
            .conditional(
                isSelected,
                ifTrue = {
                    background(
                        color = VonageVideoTheme.colors.primary,
                        shape = VonageVideoTheme.shapes.medium,
                    )
                },
                ifFalse = {
                    border(
                        width = VonageVideoTheme.dimens.borderWidthThin,
                        color = VonageVideoTheme.colors.surface,
                        shape = VonageVideoTheme.shapes.medium,
                    )
                },
            )
            .padding(vertical = VonageVideoTheme.dimens.paddingSmall, horizontal = VonageVideoTheme.dimens.paddingDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall, Alignment.CenterVertically),
    ) {
        BadgedBox(
            badge = {
                if (badgeVisible) {
                    Badge(
                        containerColor = VonageVideoTheme.colors.primary,
                        contentColor = Color.White,
                    ) {
                        Text(
                            modifier = Modifier
                                .testTag("BOTTOM_BAR_CHAT_BADGE"),
                            text = "$badgeCount",
                        )
                    }
                }
            },
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = defaultColor,
                modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault),
            )
        }

        Text(
            text = label,
            color = defaultColor,
            style = VonageVideoTheme.typography.bodyBase,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewLightDark
@Composable
internal fun MoreActionsGridPreview() {
    VonageVideoTheme {
        Column(
            modifier = Modifier
                .background(VonageVideoTheme.colors.background)
                .padding(VonageVideoTheme.dimens.paddingDefault),
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall),
        ) {
            ActionCell(
                icon = VividIcons.Solid.Blur,
                label = "Sample label",
                isSelected = false,
                badgeCount = 12,
                onClickCell = {},
            )
            ActionCell(
                icon = VividIcons.Solid.Blur,
                label = "Sample label",
                isSelected = true,
                onClickCell = {},
            )
        }
    }
}
