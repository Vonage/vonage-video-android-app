package com.vonage.android.compose.preview

import android.view.View
import androidx.compose.runtime.Composable
import com.vonage.android.kotlin.model.Participant
import com.vonage.android.kotlin.model.VideoSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.security.SecureRandom

@Composable
fun buildParticipants(count: Int): List<Participant> {
    val participants = mutableListOf<Participant>()
    for (i in 1..count) {
        val sampleParticipant = object : Participant {
            override val id: String = SecureRandom().nextInt().toString()
            override val isPublisher: Boolean
                get() = false
            override val creationTime: Long
                get() = 123L
            override val videoSource: VideoSource = VideoSource.CAMERA
            override var name: String = "Name Sample $i"
            override val isMicEnabled: MutableStateFlow<Boolean> = MutableStateFlow(false)
            override val isTalking: MutableStateFlow<Boolean> = MutableStateFlow(false)
            override val audioLevel: StateFlow<Float> = MutableStateFlow(0F)
            override val isCameraEnabled: MutableStateFlow<Boolean> = MutableStateFlow(true)
            override val view: View = previewCamera()
            override fun changeVisibility(visible: Boolean) {
                TODO("Not yet implemented")
            }

            override fun clean(session: com.opentok.android.Session) {
                TODO("Not yet implemented")
            }
        }
        participants += sampleParticipant
    }
    return participants
}
