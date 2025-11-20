package com.vonage.android.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
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
) {
    val defaultColor = VonageVideoTheme.colors.secondary

    Column(
        modifier = modifier
            .height(96.dp)
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
                        width = 1.dp,
                        color = VonageVideoTheme.colors.surface,
                        shape = VonageVideoTheme.shapes.medium,
                    )
                },
            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = defaultColor,
            modifier = Modifier.size(24.dp),
        )

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ActionCell(
                icon = VividIcons.Solid.Blur,
                label = "Sample label",
                isSelected = false,
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
