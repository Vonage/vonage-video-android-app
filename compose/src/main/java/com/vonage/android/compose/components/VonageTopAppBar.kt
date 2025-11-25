package com.vonage.android.compose.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.compose.icons.BackIcon
import com.vonage.android.compose.theme.VonageVideoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VonageTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        colors = topAppBarColors(
            containerColor = VonageVideoTheme.colors.surface,
        ),
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
