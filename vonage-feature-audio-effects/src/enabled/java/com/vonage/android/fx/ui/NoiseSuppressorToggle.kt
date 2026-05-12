package com.vonage.android.fx.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Headset2

@Composable
fun NoiseSuppressorToggle(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(VonageVideoTheme.dimens.paddingDefault)
            .border(
                width = 1.dp,
                color = VonageVideoTheme.colors.surface,
                shape = RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = VividIcons.Solid.Headset2,
            contentDescription = null,
            tint = VonageVideoTheme.colors.onSurface,
            modifier = Modifier
                .padding(start = VonageVideoTheme.dimens.paddingSmall)
                .size(24.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(VonageVideoTheme.dimens.paddingDefault)
        ) {
            Text(
                text = title,
                style = VonageVideoTheme.typography.bodyBaseSemibold,
                color = VonageVideoTheme.colors.secondary,
            )
        }
        Spacer(modifier = Modifier.width(VonageVideoTheme.dimens.spaceDefault))
        Switch(
            modifier = Modifier
                .padding(end = 8.dp),
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = VonageVideoTheme.colors.primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = VonageVideoTheme.colors.border,
                uncheckedBorderColor = VonageVideoTheme.colors.border,
            ),
        )
    }
}

@PreviewLightDark
@Composable
internal fun NoiseSuppressorTogglePreview() {
    VonageVideoTheme {
        NoiseSuppressorToggle(
            title = "Apply advanced noise suppression",
            isChecked = true,
            onCheckedChange = {},
        )
    }
}
