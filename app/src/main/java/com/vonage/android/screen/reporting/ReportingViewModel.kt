package com.vonage.android.screen.reporting

import android.net.Uri
import android.view.Window
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.ReportingRepository
import com.vonage.android.data.network.ReportDataRequest
import com.vonage.android.data.network.ReportResponseData
import com.vonage.android.kotlin.VonageVideoClient
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
    private val vonageVideoClient: VonageVideoClient,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportIssueScreenUiState())
    val uiState: StateFlow<ReportIssueScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        initialValue = ReportIssueScreenUiState(),
    )

    fun processImage(uri: Uri) {
        viewModelScope.launch {
            _uiState.update { uiState -> uiState.copy(isProcessingAttachment = true) }
            imageProcessor.extractImageFromUri(uri)
                .onSuccess { screenshot ->
                    _uiState.update { uiState ->
                        uiState.copy(attachment = screenshot, isProcessingAttachment = false)
                    }
                }
                .onFailure {
                    _uiState.update { uiState -> uiState.copy(attachment = null, isProcessingAttachment = false) }
                }
        }
    }

    fun processScreenshot(window: Window) {
        _uiState.update { uiState -> uiState.copy(isProcessingAttachment = true) }
        viewModelScope.launch {
            imageProcessor.extractImageFromWindow(window)
                .onSuccess { image ->
                    _uiState.update { uiState ->
                        uiState.copy(attachment = image, isProcessingAttachment = false)
                    }
                }
                .onFailure {
                    _uiState.update { uiState -> uiState.copy(attachment = null, isProcessingAttachment = false) }
                }
        }
    }

    fun onRemoveAttachment() {
        _uiState.update { uiState -> uiState.copy(attachment = null) }
    }

    fun sendReport(title: String, name: String, issue: String, imageBitmap: ImageBitmap?) {
        viewModelScope.launch {
            if (!validateFields(title, name, issue)) return@launch
            _uiState.update { uiState -> uiState.copy(isSending = true, isError = false) }
            val attachment = imageProcessor.encodeImageToBase64(imageBitmap)
            reportingRepository.sendReport(
                ReportDataRequest(
                    title = title,
                    name = name,
                    issue = issue + vonageVideoClient.debugDump(),
                    attachment = attachment,
                )
            )
                .onSuccess {
                    _uiState.update { uiState -> uiState.copy(
                        isError = false,
                        isSuccess = it.toIssueData(),
                        isSending = false,
                    ) }
                }
                .onFailure { _uiState.update { uiState -> uiState.copy(isError = true, isSending = false) } }
        }
    }

    fun reset() {
        _uiState.update { _ -> ReportIssueScreenUiState() }
    }

    fun updateTitle(title: String) {
        _uiState.update { uiState ->
            uiState.copy(
                title = title,
                isTitleValid = title.isNotEmptyAndMaxLength(TITLE_MAX_LENGTH),
            )
        }
    }

    fun updateUsername(username: String) {
        _uiState.update { uiState ->
            uiState.copy(
                userName = username,
                isUsernameValid = username.isNotEmptyAndMaxLength(NAME_MAX_LENGTH),
            )
        }
    }

    fun updateDescription(description: String) {
        _uiState.update { uiState ->
            uiState.copy(
                description = description,
                isDescriptionValid = description.isNotEmptyAndMaxLength(DESCRIPTION_MAX_LENGTH),
            )
        }
    }

    private fun validateFields(title: String, username: String, description: String): Boolean {
        val titleValid = title.isNotEmptyAndMaxLength(TITLE_MAX_LENGTH)
        val userNameValid = username.isNotEmptyAndMaxLength(NAME_MAX_LENGTH)
        val descriptionValid = description.isNotEmptyAndMaxLength(DESCRIPTION_MAX_LENGTH)
        _uiState.update { uiState ->
            uiState.copy(
                isTitleValid = titleValid,
                isUsernameValid = userNameValid,
                isDescriptionValid = descriptionValid,
            )
        }
        return titleValid && userNameValid && descriptionValid
    }

    private fun ReportResponseData.toIssueData(): IssueData = IssueData(
        message = message,
        ticketUrl = ticketUrl,
    )

    private fun String.isNotEmptyAndMaxLength(maxLength: Int): Boolean =
        isNotEmpty() && length <= maxLength

    companion object {
        private const val STOP_TIMEOUT_MILLIS = 2000L
        const val TITLE_MAX_LENGTH = 100
        const val NAME_MAX_LENGTH = 100
        const val DESCRIPTION_MAX_LENGTH = 1000
    }
}

@Stable
data class ReportIssueScreenUiState(
    val title: String = "",
    val isTitleValid: Boolean = true,
    val userName: String = "",
    val isUsernameValid: Boolean = true,
    val description: String = "",
    val isDescriptionValid: Boolean = true,
    val attachment: ImageBitmap? = null,
    val isProcessingAttachment: Boolean = false,
    val isSending: Boolean = false,
    val isSuccess: IssueData? = null,
    val isError: Boolean = false,
)

data class IssueData(
    val message: String,
    val ticketUrl: String,
)
