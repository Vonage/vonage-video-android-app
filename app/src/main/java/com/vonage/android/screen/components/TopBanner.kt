package com.vonage.android.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.BuildConfig
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.join.JoinMeetingRoomTestTags.VONAGE_ICON_TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBanner(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
) {
    TopAppBar(
        modifier = modifier,
        colors = topAppBarColors(
            containerColor = VonageVideoTheme.colors.surface,
        ),
        navigationIcon = {
            onBack?.let {
                IconButton(
                    onClick = onBack,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            }
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                VonageIcon(
                    modifier = Modifier
                        .size(32.dp)
                        .testTag(VONAGE_ICON_TAG)
                )
                Text(
                    text = BuildConfig.VERSION_NAME,
                    style = VonageVideoTheme.typography.label,
                    color = VonageVideoTheme.colors.textPrimaryDisabled,
                )
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
        )
    }
}
