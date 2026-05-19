package com.vonage.android.settings

import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.VideoCodec
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CallSettingsHolder {

    private val _call = MutableStateFlow<CallFacade?>(null)
    val call: StateFlow<CallFacade?> = _call.asStateFlow()

    private val _senderStatsEnabled = MutableStateFlow(true)
    val senderStatsEnabled: StateFlow<Boolean> = _senderStatsEnabled.asStateFlow()

    private val _opusDtxEnabled = MutableStateFlow(true)
    val opusDtxEnabled: StateFlow<Boolean> = _opusDtxEnabled.asStateFlow()

    private val _videoBitrateConfig = MutableStateFlow(
        VideoBitrateConfig(
            preset = VideoBitratePreset.DEFAULT,
            maxBitrate = VideoBitratePreset.DEFAULT.defaultMaxBitrate,
        ),
    )
    val videoBitrateConfig: StateFlow<VideoBitrateConfig> = _videoBitrateConfig.asStateFlow()

    private val _degradationPreference = MutableStateFlow(DegradationPreference.NOT_SET)
    val degradationPreference: StateFlow<DegradationPreference> = _degradationPreference.asStateFlow()

    private val _captureFrameRate = MutableStateFlow(CaptureFrameRate.FPS_15)
    val captureFrameRate: StateFlow<CaptureFrameRate> = _captureFrameRate.asStateFlow()

    private val _captureResolution = MutableStateFlow<CaptureResolution?>(null)
    val captureResolution: StateFlow<CaptureResolution?> = _captureResolution.asStateFlow()

    private val _publisherAudioFallbackEnabled = MutableStateFlow(true)
    val publisherAudioFallbackEnabled: StateFlow<Boolean> = _publisherAudioFallbackEnabled.asStateFlow()

    private val _subscriberAudioFallbackEnabled = MutableStateFlow(true)
    val subscriberAudioFallbackEnabled: StateFlow<Boolean> = _subscriberAudioFallbackEnabled.asStateFlow()

    private val _preferredVideoCodecOrder = MutableStateFlow<List<VideoCodec>?>(null)
    val preferredVideoCodecOrder: StateFlow<List<VideoCodec>?> = _preferredVideoCodecOrder.asStateFlow()

    private val _audioBitrate = MutableStateFlow<Int?>(null)
    val audioBitrate: StateFlow<Int?> = _audioBitrate.asStateFlow()

    fun updateSenderStatsEnabled(enabled: Boolean) {
        _senderStatsEnabled.value = enabled
    }

    fun updateOpusDtx(enabled: Boolean) {
        _opusDtxEnabled.value = enabled
    }

    fun updateVideoBitrateConfig(config: VideoBitrateConfig) {
        _videoBitrateConfig.value = config
        _call.value?.setVideoBitrate(config)
    }

    fun updateDegradationPreference(preference: DegradationPreference) {
        _degradationPreference.value = preference
        _call.value?.setDegradationPreference(preference)
    }

    fun updateCaptureFrameRate(frameRate: CaptureFrameRate) {
        _captureFrameRate.value = frameRate
    }

    fun updateCaptureResolution(resolution: CaptureResolution?) {
        _captureResolution.value = resolution
    }

    fun updatePublisherAudioFallback(enabled: Boolean) {
        _publisherAudioFallbackEnabled.value = enabled
    }

    fun updateSubscriberAudioFallback(enabled: Boolean) {
        _subscriberAudioFallbackEnabled.value = enabled
    }

    fun updatePreferredVideoCodecOrder(order: List<VideoCodec>?) {
        _preferredVideoCodecOrder.value = order
    }

    fun updateAudioBitrate(bitrate: Int?) {
        _audioBitrate.value = bitrate
    }

    fun clear() {
        _call.value = null
        _senderStatsEnabled.value = true
        _videoBitrateConfig.value = VideoBitrateConfig(
            preset = VideoBitratePreset.DEFAULT,
            maxBitrate = VideoBitratePreset.DEFAULT.defaultMaxBitrate,
        )
        _degradationPreference.value = DegradationPreference.NOT_SET
        _captureFrameRate.value = CaptureFrameRate.FPS_15
        _captureResolution.value = null
        _publisherAudioFallbackEnabled.value = true
        _subscriberAudioFallbackEnabled.value = true
        _preferredVideoCodecOrder.value = null
        _audioBitrate.value = null
        _opusDtxEnabled.value = true
    }

    fun bind(call: CallFacade) {
        _call.value = call
    }
}
