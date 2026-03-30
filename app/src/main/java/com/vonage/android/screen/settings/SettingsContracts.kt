package com.vonage.android.screen.settings

import android.net.Uri
import com.vonage.android.core.ActionDependencies
import com.vonage.android.core.ViewEvent
import com.vonage.android.data.ClientLogsRepository
import com.vonage.android.settings.CallSettingsHolder
import kotlinx.coroutines.CoroutineScope

sealed interface SettingsViewEvent : ViewEvent {
    data object LogsSent : SettingsViewEvent
    data object NoLogsAvailable : SettingsViewEvent
    data object LogsSendFailed : SettingsViewEvent
    data class ShareLogs(val uri: Uri) : SettingsViewEvent
    data object NoLogsToShare : SettingsViewEvent
}

class SettingsActionDependencies(
    override val coroutineScope: CoroutineScope,
    val callSettingsHolder: CallSettingsHolder,
    val clientLogsRepository: ClientLogsRepository,
) : ActionDependencies()
