package com.vonage.android.screen.settings

import com.vonage.android.core.ActionDependencies
import com.vonage.android.shared.ViewEvent
import com.vonage.android.settings.CallSettingsHolder
import kotlinx.coroutines.CoroutineScope

sealed interface SettingsViewEvent : ViewEvent

class SettingsActionDependencies(
    override val coroutineScope: CoroutineScope,
    val callSettingsHolder: CallSettingsHolder,
) : ActionDependencies()
