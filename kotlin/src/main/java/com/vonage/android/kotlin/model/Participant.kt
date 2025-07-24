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
    override val view: View,
    val cycleCamera: () -> Unit,
    val blurLevel: BlurLevel,
    val setCameraBlur: (BlurLevel) -> Unit,
) : Participant

data class VeraSubscriber(
    override val id: String,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View,
) : Participant
