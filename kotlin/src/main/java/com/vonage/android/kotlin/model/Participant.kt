package com.vonage.android.kotlin.model

import android.view.View

interface Participant {
    val id: String
    val name: String
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    val view: View
}
