package com.vonage.android.kotlin

import android.view.View

interface Participant {
    var name: String
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    val view: View
    fun toggleAudio(): Boolean
    fun toggleVideo(): Boolean
}
