package com.vonage.android.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.components.VonageTopAppBar
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.landing.LandingScreenTestTags.VONAGE_ICON_TAG

@Composable
fun TopBanner(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    VonageTopAppBar(
        modifier = modifier,
        onBack = onBack,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
            ) {
                VonageIcon(
                    modifier = Modifier
                        .size(32.dp)
                        .testTag(VONAGE_ICON_TAG)
                )
                content()
            }
        }
    )
}

@PreviewLightDark
@Composable
internal fun TopBannerPreview() {
    VonageVideoTheme {
        TopBanner(
            onBack = {},
        ) {
            Text("Test")
        }
    }
}
