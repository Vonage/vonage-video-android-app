package com.vonage.android.compose.layout

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.vonage.android.compose.theme.VonageVideoTheme

@Composable
fun TwoPaneScaffold(
    firstPane: @Composable () -> Unit,
    secondPane: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass,
    topBar: @Composable () -> Unit = {},
    firstPaneBackground: Color = VonageVideoTheme.colors.surface,
    secondPaneBackground: Color = VonageVideoTheme.colors.background,
) {
    val isHorizontalLayout = windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)

    Scaffold(
        modifier = modifier,
        topBar = {
            if (!isHorizontalLayout) {
                topBar()
            }
        },
    ) { contentPadding ->
        if (isHorizontalLayout) {
            HorizontalLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(contentPadding),
                topBar = topBar,
                firstPane = firstPane,
                firstPaneBackground = firstPaneBackground,
                secondPane = secondPane,
                secondPaneBackground = secondPaneBackground,
            )
        } else {
            VerticalLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .consumeWindowInsets(contentPadding),
                firstPaneBackground = firstPaneBackground,
                firstPane = firstPane,
                secondPaneBackground = secondPaneBackground,
                secondPane = secondPane
            )
        }
    }
}

@Composable
private fun VerticalLayout(
    firstPaneBackground: Color,
    firstPane: @Composable (() -> Unit),
    secondPaneBackground: Color,
    secondPane: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(top = VonageVideoTheme.dimens.spaceXXLarge)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(firstPaneBackground)
                .padding(VonageVideoTheme.dimens.paddingLarge),
            contentAlignment = Alignment.Center,
        ) {
            firstPane()
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(secondPaneBackground)
                .padding(VonageVideoTheme.dimens.paddingLarge),
            contentAlignment = Alignment.Center,
        ) {
            secondPane()
        }
    }
}

@Composable
private fun HorizontalLayout(
    firstPane: @Composable () -> Unit,
    secondPane: @Composable () -> Unit,
    firstPaneBackground: Color,
    secondPaneBackground: Color,
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
) {
    Row(modifier = modifier) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(firstPaneBackground),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .padding(VonageVideoTheme.dimens.paddingXLarge)
                    .align(Alignment.TopStart)
            ) {
                topBar()
            }
            Box(
                modifier = Modifier
                    .padding(start = VonageVideoTheme.dimens.spaceXXXLarge)
                    .align(Alignment.Center)
            ) {
                firstPane()
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(secondPaneBackground),
            contentAlignment = Alignment.Center,
        ) {
            secondPane()
        }
    }
}
