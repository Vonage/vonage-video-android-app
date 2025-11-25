package com.vonage.android.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
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
) {
    VonageTopAppBar(
        modifier = modifier,
        onBack = onBack,
        title = {
            VonageIcon(
                modifier = Modifier
                    .size(32.dp)
                    .testTag(VONAGE_ICON_TAG)
            )
        }
    )
}

@PreviewLightDark
@Composable
internal fun TopBannerPreview() {
    VonageVideoTheme {
        TopBanner(
            onBack = {},
        )
    }
}
