package com.vonage.android.kotlin.internal

import android.util.Log
import com.vonage.android.kotlin.ext.name
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap

/**
 * Processes audio level updates in the background and provides moving averages per subscriber.
 * This improves performance by avoiding UI thread blocking when processing frequent audio updates (every 50ms).
 */
class AudioLevelProcessor(
    private val coroutineScope: CoroutineScope,
    private val windowSize: Int = 5, // Number of samples for moving average
    private val significantChangeThreshold: Float = 0.15f // Only emit if change is significant
) {
    
    private data class AudioLevelUpdate(
        val subscriberId: String,
        val audioLevel: Float
    )
    
    // Channel to process audio updates in background
    private val audioUpdateChannel = Channel<AudioLevelUpdate>(capacity = 50) // Limited capacity to prevent memory issues
    
    // Moving average buffers per subscriber
    private val audioBuffers = ConcurrentHashMap<String, CircularBuffer>()
    
    // Current smoothed audio levels per subscriber
    private val _audioLevels = ConcurrentHashMap<String, MutableStateFlow<Float>>()
    
    init {
        // Process audio updates in background coroutine
        coroutineScope.launch(Dispatchers.Default) {
            audioUpdateChannel.consumeAsFlow().distinctUntilChanged().collect { update ->
                Log.d("processAudioUpdate", "${update.subscriberId} -> ${update.audioLevel} [ ${Thread.currentThread()} ]")
                processAudioLevel(update.subscriberId, update.audioLevel)
            }
        }
    }
    
    /**
     * Submit a new audio level update for processing.
     * This is called from the main thread but processing happens in background.
     */
    fun onAudioLevelUpdate(subscriberId: String, audioLevel: Float) {
        // Use trySend to avoid blocking if channel is full
        val result = audioUpdateChannel.trySend(AudioLevelUpdate(subscriberId, audioLevel))
        if (result.isFailure) {
            // Channel is full, skip this update to prevent blocking
            // This is acceptable since audio levels are frequent (every 50ms)
        }
    }
    
    /**
     * Get the smoothed audio level flow for a specific subscriber.
     */
    fun getAudioLevelFlow(subscriberId: String): StateFlow<Float> {
        return _audioLevels.getOrPut(subscriberId) {
            MutableStateFlow(0f)
        }
    }
    
    /**
     * Remove audio level tracking for a subscriber when they leave.
     */
    fun removeSubscriber(subscriberId: String) {
        audioBuffers.remove(subscriberId)
        _audioLevels.remove(subscriberId)
    }
    
    private fun processAudioLevel(subscriberId: String, audioLevel: Float) {
        // Get or create buffer for this subscriber
        val buffer = audioBuffers.getOrPut(subscriberId) {
            CircularBuffer(windowSize)
        }
        
        // Add new sample and calculate moving average
        buffer.add(audioLevel)
        val smoothedLevel = buffer.getAverage()
        
        // Get or create StateFlow for this subscriber
        val stateFlow = _audioLevels.getOrPut(subscriberId) {
            MutableStateFlow(0f)
        }
        
        // Only update if the change is significant to avoid unnecessary UI updates
        val currentLevel = stateFlow.value
        if (kotlin.math.abs(smoothedLevel - currentLevel) >= significantChangeThreshold) {
            stateFlow.value = smoothedLevel
        }
    }
    
    /**
     * Circular buffer for efficient moving average calculation.
     */
    private class CircularBuffer(private val capacity: Int) {
        private val buffer = FloatArray(capacity)
        private var size = 0
        private var index = 0
        private var sum = 0f
        
        fun add(value: Float) {
            if (size < capacity) {
                buffer[index] = value
                sum += value
                size++
            } else {
                // Replace oldest value
                sum = sum - buffer[index] + value
                buffer[index] = value
            }
            index = (index + 1) % capacity
        }
        
        fun getAverage(): Float = if (size > 0) sum / size else 0f
    }
}