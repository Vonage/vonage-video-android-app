package com.vonage.android.kotlin.model

import android.view.View

data class VeraPublisher(
    override val id: String,
    override var name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View
) : Participant
