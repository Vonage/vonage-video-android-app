package com.vonage.android.screen.waiting.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Gear
import com.vonage.android.screen.components.TopBanner
import com.vonage.android.screen.waiting.WaitingRoomActions

@Composable
internal fun WaitingRoomTopBar(
    actions: WaitingRoomActions,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopBanner(
        modifier = modifier,
        onBack = actions.onBack,
        content = {
            Text(
                text = stringResource(R.string.waiting_room_prepare_to_join),
                style = VonageVideoTheme.typography.heading4,
                color = VonageVideoTheme.colors.textSecondary,
            )
        },
        actions = {
            IconButton(
                modifier = Modifier,
                onClick = navigateToSettings,
            ) {
                Icon(
                    imageVector = VividIcons.Solid.Gear,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.onSurface,
                    modifier = Modifier.size(VonageVideoTheme.dimens.iconSizeDefault)
                )
            }
        }
    )
}
