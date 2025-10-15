package com.vonage.android.compose.preview

import android.view.View
import androidx.compose.runtime.Composable
import com.vonage.android.compose.preview.previewCamera
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VideoSource
import kotlinx.coroutines.flow.MutableStateFlow
import java.security.SecureRandom

@Composable
fun buildParticipants(count: Int): List<Participant> {
    val participants = mutableListOf<Participant>()
    for (i in 1..count) {
        val sampleParticipant = object : Participant {
            override val id: String = SecureRandom().nextInt().toString()
            override val videoSource: VideoSource = VideoSource.CAMERA
            override var name: String = "Name Sample $i"
            override val isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
            override val isSpeaking: MutableStateFlow<Boolean> = MutableStateFlow(false)
            override val isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
            override val view: View = previewCamera()
        }
        participants += sampleParticipant
    }
    return participants
}
