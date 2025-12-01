package com.vonage.android.kotlin.model

import android.view.View
import androidx.compose.runtime.Stable
import com.opentok.android.Session
import kotlinx.coroutines.flow.StateFlow

/**
 * Base interface for all participants in a video call.
 *
 * Represents both publishers (local user) and subscribers (remote users).
 * Provides reactive state flows for audio/video state and audio levels.
 */
@Stable
interface Participant {
    /** Unique identifier (stream ID for subscribers, constant for publisher) */
    val id: String
    
    /** True if this is the local publisher, false if remote subscriber */
    val isPublisher: Boolean
    
    /** Timestamp when the participant joined (milliseconds since epoch) */
    val creationTime: Long
    
    /** Source of the video stream (camera or screen share) */
    val videoSource: VideoSource
    
    /** Display name of the participant */
    val name: String
    
    /** StateFlow indicating if microphone is enabled */
    val isMicEnabled: StateFlow<Boolean>
    
    /** StateFlow indicating if camera is enabled */
    val isCameraEnabled: StateFlow<Boolean>
    
    /** StateFlow indicating if participant is currently talking */
    val isTalking: StateFlow<Boolean>
    
    /** StateFlow of audio level (0.0 to 1.0) */
    val audioLevel: StateFlow<Float>
    
    /** Android View for rendering the video stream */
    val view: View
    
    /**
     * Changes the visibility state for bandwidth optimization.
     *
     * @param visible True to enable video, false to disable
     */
    fun changeVisibility(visible: Boolean) {}
    
    /**
     * Cleans up resources when removing the participant.
     *
     * @param session The session to unsubscribe from
     */
    fun clean(session: Session) {}
}

/**
 * Extended interface for publisher participants with camera controls.
 */
@Stable
interface PublisherParticipant : Participant {
    override val isPublisher: Boolean
        get() = true
    
    /** StateFlow of current camera type (front/back) */
    val camera: StateFlow<CameraType>
    
    /** StateFlow of current background blur level */
    val blurLevel: StateFlow<BlurLevel>
    
    /** Toggles video on/off */
    fun toggleVideo()
    
    /** Toggles audio on/off */
    fun toggleAudio()
    
    /** Switches between front and back camera */
    fun cycleCamera()
    
    /** Cycles through blur levels (none -> low -> high -> none) */
    fun cycleCameraBlur()
    
    /** Cleans up publisher resources */
    fun clean()
}

/**
 * Enumeration of camera types.
 */
enum class CameraType(val index: Int) {
    UNKNOWN(-1),
    BACK(0),
    FRONT(1);

    companion object {
        private val map = entries.toTypedArray().associateBy(CameraType::index)
        infix fun fromInt(index: Int): CameraType? = map[index]
    }
}

enum class VideoSource {
    CAMERA,
    SCREEN
}
