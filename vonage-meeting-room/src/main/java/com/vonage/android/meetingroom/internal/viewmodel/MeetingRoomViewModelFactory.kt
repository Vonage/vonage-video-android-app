package com.vonage.android.meetingroom.internal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vonage.android.meetingroom.api.MeetingRoomConfig
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer

internal class MeetingRoomViewModelFactory(
    private val roomName: String,
    private val applicationContext: Context,
    private val config: MeetingRoomConfig,
    private val isDebug: Boolean = false,
) : ViewModelProvider.Factory {

    private val container: MeetingRoomContainer by lazy {
        MeetingRoomContainer(
            applicationContext = applicationContext,
            config = config,
            isDebug = isDebug,
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingRoomViewModel::class.java)) {
            return MeetingRoomViewModel(
                roomName = roomName,
                container = container,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
