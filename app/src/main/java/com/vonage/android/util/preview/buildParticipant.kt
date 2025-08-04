package com.vonage.android.util.preview

import android.view.View
import androidx.compose.runtime.Composable
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.kotlin.model.Participant
import kotlin.random.Random

@Composable
fun buildParticipants(count: Int): List<Participant> {
    val participants = mutableListOf<Participant>()
    for (i in 1..count) {
        val sampleParticipant = object : Participant {
            override val id: String = Random(i).toString()
            override var name: String = "Name Sample $i"
            override val isMicEnabled: Boolean = false
            override val isCameraEnabled: Boolean = true
            override val view: View = previewCamera()
            override val isSpeaking: Boolean = false
        }
        participants += sampleParticipant
    }
    return participants
}
