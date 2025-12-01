package com.vonage.android.screen.goodbye.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.R
import com.vonage.android.compose.components.VonageButton
import com.vonage.android.compose.components.VonageOutlinedButton
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.line.Enter
import com.vonage.android.screen.goodbye.GoodbyeScreenActions

@Composable
fun RejoiningContainer(
    actions: GoodbyeScreenActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
    ) {
        Text(
            text = stringResource(R.string.goodbye_rejoin_title),
            style = VonageVideoTheme.typography.heading4,
            color = VonageVideoTheme.colors.textSecondary,
        )

        VonageButton(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.goodbye_rejoin_button_label),
            onClick = actions.onReEnter,
            leadingIcon = {
                Icon(
                    imageVector = VividIcons.Line.Enter,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeSmall),
                )
            }
        )

        VonageOutlinedButton(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(R.string.goodbye_return_home_button_label),
            onClick = actions.onGoHome,
        )
    }
}

@PreviewLightDark
@Composable
internal fun RejoiningContainerPreview() {
    VonageVideoTheme {
        RejoiningContainer(
            actions = GoodbyeScreenActions()
        )
    }
}