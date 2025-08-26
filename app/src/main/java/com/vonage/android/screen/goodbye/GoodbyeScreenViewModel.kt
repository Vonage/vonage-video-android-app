package com.vonage.android.screen.goodbye

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.data.Archive
import com.vonage.android.data.ArchiveRepository
import com.vonage.android.data.ArchiveStatus
import com.vonage.android.util.coroutines.CoroutinePoller
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = GoodbyeScreenViewModelFactory::class)
class GoodbyeScreenViewModel @AssistedInject constructor(
    @Assisted val roomName: String,
    private val archiveRepository: ArchiveRepository,
    private val downloadManager: DownloadManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoodbyeScreenUiState>(GoodbyeScreenUiState.Idle)
    val uiState: StateFlow<GoodbyeScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = GoodbyeScreenUiState.Idle,
    )

    init {
        viewModelScope.launch {
            CoroutinePoller<Unit>(
                dispatcher = Dispatchers.IO,
                fetchData = {
                    archiveRepository.getRecordings(roomName)
                        .onSuccess { archives ->
                            archives
                                .count { archive -> archive.status == ArchiveStatus.PENDING }
                                .let { if (it == 0) cancel() }
                            _uiState.value = GoodbyeScreenUiState.Content(
                                archives = archives.toImmutableList()
                            )
                        }
                },
            ).poll(POLLING_DELAY).collect()
        }
    }

    fun downloadArchive(archive: Archive) {
        if (archive.status == ArchiveStatus.AVAILABLE) {
            downloadManager.downloadByUrl(
                url = archive.url,
            )
        }
    }

    private companion object {
        const val POLLING_DELAY = 2000L
        const val SUBSCRIBED_TIMEOUT_MS: Long = 5_000
    }
}

@AssistedFactory
interface GoodbyeScreenViewModelFactory {
    fun create(roomName: String): GoodbyeScreenViewModel
}

sealed interface GoodbyeScreenUiState {
    data class Content(
        val archives: ImmutableList<Archive>,
    ) : GoodbyeScreenUiState

    data object Idle : GoodbyeScreenUiState
}
