package com.vonage.android.kotlin

import android.view.View
import com.opentok.android.Publisher

interface Participant {
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    fun getView(): View
    fun getName(): String
}

class VeraPublisher constructor(private val publisher: Publisher) : Participant {

    override val isMicEnabled: Boolean = true
    override val isCameraEnabled: Boolean = false

    override fun getView(): View {
        return publisher.view
    }

    override fun getName(): String {
        return "Vera User"
    }
}
