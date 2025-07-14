package com.vonage.android.kotlin

import android.view.View

interface Participant {
    var isMicEnabled: Boolean
    var isCameraEnabled: Boolean
    val view: View
    var name: String
    fun toggleAudio(): Boolean
    fun toggleVideo(): Boolean
}
