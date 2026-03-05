package com.vonage.android.settings

import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.model.CallFacade

@Stable
data class SettingsUiState(
    val appVersion: String = "",
    val sdkVersion: String = "",
    val call: CallFacade? = null,
    val senderStatsEnabled: Boolean = true,
)
