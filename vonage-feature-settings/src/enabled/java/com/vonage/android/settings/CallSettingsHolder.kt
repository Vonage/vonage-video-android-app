package com.vonage.android.settings

import com.vonage.android.kotlin.model.CallFacade
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallSettingsHolder @Inject constructor() {

    private val _call = MutableStateFlow<CallFacade?>(null)
    val call: StateFlow<CallFacade?> = _call.asStateFlow()

    private val _senderStatsEnabled = MutableStateFlow(true)
    val senderStatsEnabled: StateFlow<Boolean> = _senderStatsEnabled.asStateFlow()

    fun updateSenderStatsEnabled(enabled: Boolean) {
        _senderStatsEnabled.value = enabled
    }

    fun clear() {
        _call.value = null
        _senderStatsEnabled.value = true
    }

    fun bind(call: CallFacade) {
        _call.value = call
    }
}
