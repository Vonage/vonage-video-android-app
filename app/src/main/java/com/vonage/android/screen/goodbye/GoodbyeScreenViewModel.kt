package com.vonage.android.screen.goodbye

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.archiving.data.ArchiveRepository
import com.vonage.android.di.IODispatcher
import com.vonage.android.util.DownloadManager
import com.vonage.android.util.coroutines.CoroutinePollerProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
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
    private val coroutinePollerProvider: CoroutinePollerProvider<Unit>,
    @param:IODispatcher private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _uiState = MutableStateFlow<GoodbyeScreenUiState>(GoodbyeScreenUiState.Idle)
    val uiState: StateFlow<GoodbyeScreenUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SUBSCRIBED_TIMEOUT_MS),
        initialValue = GoodbyeScreenUiState.Idle,
    )

    init {
        viewModelScope.launch {
            coroutinePollerProvider.get(
                dispatcher = dispatcher,
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
                        .onFailure { cancel() }
                },
            ).poll(POLLING_DELAY).collect()
        }
    }

    fun downloadArchive(archive: Archive) {
        if (archive.status == ArchiveStatus.AVAILABLE) {
            downloadManager.downloadByUrl(archive.url)
        }
    }

    private companion object {
        const val POLLING_DELAY = 2000L
        const val SUBSCRIBED_TIMEOUT_MS: Long = 5_000
    }
}

@AssistedFactory
fun interface GoodbyeScreenViewModelFactory {
    fun create(roomName: String): GoodbyeScreenViewModel
}

@Stable
sealed interface GoodbyeScreenUiState {
    data class Content(
        val archives: ImmutableList<Archive>,
    ) : GoodbyeScreenUiState

    data object Idle : GoodbyeScreenUiState
}
