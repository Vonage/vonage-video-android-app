package com.vonage.android.settings

import androidx.compose.runtime.Stable
import com.vonage.android.kotlin.model.CallFacade
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.kotlin.model.VideoBitrateConfig
import com.vonage.android.kotlin.model.VideoBitratePreset
import com.vonage.android.kotlin.model.VideoCodec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Stable
class CallSettingsHolder(
    private val storage: CallSettingsStorage = NoOpCallSettingsStorage(),
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {

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

    init {
        scope.launch {
            val p = storage.load()
            _captureFrameRate.value = p.captureFrameRate
            _captureResolution.value = p.captureResolution
            _preferredVideoCodecOrder.value = p.preferredVideoCodecOrder
            _audioBitrate.value = p.audioBitrate
            _opusDtxEnabled.value = p.opusDtxEnabled
            _publisherAudioFallbackEnabled.value = p.publisherAudioFallbackEnabled
            _subscriberAudioFallbackEnabled.value = p.subscriberAudioFallbackEnabled
            _senderStatsEnabled.value = p.senderStatsEnabled
            _videoBitrateConfig.value = p.videoBitrateConfig
            _degradationPreference.value = p.degradationPreference
        }
    }

    fun updateSenderStatsEnabled(enabled: Boolean) {
        _senderStatsEnabled.value = enabled
        save()
    }

    fun updateOpusDtx(enabled: Boolean) {
        _opusDtxEnabled.value = enabled
        save()
    }

    fun updateVideoBitrateConfig(config: VideoBitrateConfig) {
        _videoBitrateConfig.value = config
        _call.value?.setVideoBitrate(config)
        save()
    }

    fun updateDegradationPreference(preference: DegradationPreference) {
        _degradationPreference.value = preference
        _call.value?.setDegradationPreference(preference)
        save()
    }

    fun updateCaptureFrameRate(frameRate: CaptureFrameRate) {
        _captureFrameRate.value = frameRate
        save()
    }

    fun updateCaptureResolution(resolution: CaptureResolution?) {
        _captureResolution.value = resolution
        save()
    }

    fun updatePublisherAudioFallback(enabled: Boolean) {
        _publisherAudioFallbackEnabled.value = enabled
        save()
    }

    fun updateSubscriberAudioFallback(enabled: Boolean) {
        _subscriberAudioFallbackEnabled.value = enabled
        save()
    }

    fun updatePreferredVideoCodecOrder(order: List<VideoCodec>?) {
        _preferredVideoCodecOrder.value = order
        save()
    }

    fun updateAudioBitrate(bitrate: Int?) {
        _audioBitrate.value = bitrate
        save()
    }

    fun clearCall() {
        _call.value = null
    }

    fun bind(call: CallFacade) {
        _call.value = call
    }

    private fun save() {
        scope.launch { storage.save(snapshot()) }
    }

    private fun snapshot() = PersistedCallSettings(
        captureFrameRate = _captureFrameRate.value,
        captureResolution = _captureResolution.value,
        preferredVideoCodecOrder = _preferredVideoCodecOrder.value,
        audioBitrate = _audioBitrate.value,
        opusDtxEnabled = _opusDtxEnabled.value,
        publisherAudioFallbackEnabled = _publisherAudioFallbackEnabled.value,
        subscriberAudioFallbackEnabled = _subscriberAudioFallbackEnabled.value,
        senderStatsEnabled = _senderStatsEnabled.value,
        videoBitrateConfig = _videoBitrateConfig.value,
        degradationPreference = _degradationPreference.value,
    )
}
