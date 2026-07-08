package com.vonage.android.compose.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.compose.icons.BackIcon
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Gear

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VonageTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    colors: TopAppBarColors = topAppBarColors(
        containerColor = VonageVideoTheme.colors.surface,
    ),
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        colors = colors,
        navigationIcon = {
            onBack?.let {
                IconButton(onClick = onBack) {
                    BackIcon()
                }
            }
        },
        title = title,
        actions = actions,
    )
}

@PreviewLightDark
@Composable
internal fun VonageTopAppBarDefaultPreview() {
    VonageVideoTheme {
        VonageTopAppBar(title = { Text("Settings") })
    }
}

@PreviewLightDark
@Composable
internal fun VonageTopAppBarWithBackPreview() {
    VonageVideoTheme {
        VonageTopAppBar(title = { Text("Settings") }, onBack = {})
    }
}

@PreviewLightDark
@Composable
internal fun VonageTopAppBarFullPreview() {
    VonageVideoTheme {
        VonageTopAppBar(
            title = { Text("Settings") },
            onBack = {},
            actions = {
                IconButton(onClick = {}) {
                    Icon(imageVector = VividIcons.Solid.Gear, contentDescription = null)
                }
            },
        )
    }
}
