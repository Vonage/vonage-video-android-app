package com.vonage.android.screen.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.compose.theme.VonageVideoTheme

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
            VonageIcon(
                modifier = Modifier.size(32.dp)
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
