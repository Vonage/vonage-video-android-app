package com.vonage.android.kotlin.model

import android.view.View

data class VeraSubscriber(
    override val id: String,
    override val name: String,
    override val isMicEnabled: Boolean,
    override val isCameraEnabled: Boolean,
    override val view: View
) : Participant
