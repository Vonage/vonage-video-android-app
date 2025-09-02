package com.vonage.android.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CallActionsListener @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : BroadcastReceiver() {

    private val filter = IntentFilter()
        .apply {
            addAction(HANG_UP_ACTION)
        }

    private val _actions = MutableStateFlow<CallAction?>(null)
    val actions: StateFlow<CallAction?> = _actions.asStateFlow()

    init {

        ContextCompat.registerReceiver(context, this, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    fun onHangUp() {
        _actions.update { CallAction.HangUp }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == HANG_UP_ACTION) {
            onHangUp()
        }
    }

    fun stop() {
        ContextCompat.registerReceiver(context, null, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
        _actions.update { null }
    }
}

const val HANG_UP_ACTION = "com.vonage.android.VERA_HANG_UP"

sealed interface CallAction {
    data object HangUp : CallAction
}
