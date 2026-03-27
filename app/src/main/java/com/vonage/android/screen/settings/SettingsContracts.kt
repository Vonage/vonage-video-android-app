package com.vonage.android.screen.settings

import com.vonage.android.core.ActionDependencies
import com.vonage.android.core.ViewEvent
import com.vonage.android.data.ClientLogsRepository
import com.vonage.android.settings.CallSettingsHolder
import kotlinx.coroutines.CoroutineScope

sealed interface SettingsViewEvent : ViewEvent {
    data object LogsSent : SettingsViewEvent
    data object NoLogsAvailable : SettingsViewEvent
    data object LogsSendFailed : SettingsViewEvent
}

class SettingsActionDependencies(
    override val coroutineScope: CoroutineScope,
    val callSettingsHolder: CallSettingsHolder,
    val clientLogsRepository: ClientLogsRepository,
) : ActionDependencies()
