package com.vonage.android.screen.reporting

import android.net.Uri
import android.view.Window
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.ReportingRepository
import com.vonage.android.data.network.FeedbackData
import com.vonage.android.data.network.ReportResponseData
import com.vonage.android.util.ImageProcessor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportingViewModel @Inject constructor(
    private val reportingRepository: ReportingRepository,
    private val imageProcessor: ImageProcessor,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportIssueScreenUiState())
    val uiState: StateFlow<ReportIssueScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ReportIssueScreenUiState(),
    )

    fun processImage(uri: Uri) {
        _uiState.update { uiState -> uiState.copy(isProcessingScreenshot = true) }
        viewModelScope.launch {
            imageProcessor.extractImageFromUri(uri)
                .onSuccess { screenshot ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            attachment = screenshot,
                            isProcessingScreenshot = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { uiState -> uiState.copy(isProcessingScreenshot = false) }
                }
        }
    }

    fun processScreenshot(window: Window) {
        _uiState.update { uiState -> uiState.copy(isProcessingScreenshot = true) }
        viewModelScope.launch {
            imageProcessor.extractImageFromWindow(window)
                .onSuccess { image ->
                    _uiState.update { uiState ->
                        uiState.copy(
                            attachment = image,
                            isProcessingScreenshot = false,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { uiState -> uiState.copy(isProcessingScreenshot = false) }
                }
        }
    }

    fun onRemoveAttachment() {
        _uiState.update { uiState -> uiState.copy(attachment = null) }
    }

    fun sendReport(title: String, name: String, issue: String, imageBitmap: ImageBitmap?) {
        viewModelScope.launch {
            _uiState.update { uiState -> uiState.copy(isSending = true) }
            val attachment = imageProcessor.encodeImageToBase64(imageBitmap)
            reportingRepository.sendReport(
                FeedbackData(
                    title = title,
                    name = name,
                    issue = issue,
                    attachment = attachment,
                )
            )
                .onSuccess { _uiState.update { uiState -> uiState.copy(isSuccess = it.toIssueData()) } }
                .onFailure { _uiState.update { uiState -> uiState.copy(isError = true, isSending = false) } }
        }
    }

    fun reset() {
        _uiState.update { uiState -> ReportIssueScreenUiState() }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 2500L
    }
}

data class ReportIssueScreenUiState(
    val isProcessingScreenshot: Boolean = false,
    val isSending: Boolean = false,
    val isSuccess: IssueData? = null,
    val isError: Boolean = false,
    val attachment: ImageBitmap? = null,
)

fun ReportResponseData.toIssueData(): IssueData = IssueData(
    message = message,
    ticketUrl = ticketUrl,
)

data class IssueData(
    val message: String,
    val ticketUrl: String,
)
