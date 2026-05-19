package com.vonage.android.meetingroom.internal.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vonage.android.meetingroom.api.MeetingRoomPrebuilt
import com.vonage.android.meetingroom.internal.container.MeetingRoomContainer

internal class MeetingRoomViewModelFactory(
    private val applicationContext: Context,
    private val prebuilt: MeetingRoomPrebuilt,
) : ViewModelProvider.Factory {

    private val container: MeetingRoomContainer by lazy {
        MeetingRoomContainer(
            applicationContext = applicationContext,
            prebuilt = prebuilt,
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeetingRoomViewModel::class.java)) {
            return MeetingRoomViewModel(
                container = container,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
