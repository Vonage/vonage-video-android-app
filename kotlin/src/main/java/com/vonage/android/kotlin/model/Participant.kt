package com.vonage.android.kotlin.model

import android.view.View

interface Participant {
    val id: String
    val name: String
    val isMicEnabled: Boolean
    val isCameraEnabled: Boolean
    val view: View
}

data class VeraPublisher(
    override val id: String,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View
) : Participant

typealias VeraSubscriber = VeraPublisher
