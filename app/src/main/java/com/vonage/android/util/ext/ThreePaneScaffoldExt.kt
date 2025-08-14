package com.vonage.android.util.ext

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal fun ThreePaneScaffoldNavigator<Any>.isExtraPaneShow(): Boolean =
    scaffoldValue[SupportingPaneScaffoldRole.Extra] == PaneAdaptedValue.Expanded

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
internal suspend fun ThreePaneScaffoldNavigator<Any>.toggleChat() {
    if (scaffoldValue[SupportingPaneScaffoldRole.Extra] == PaneAdaptedValue.Hidden) {
        navigateTo(SupportingPaneScaffoldRole.Extra)
    } else {
        navigateBack()
    }
}
