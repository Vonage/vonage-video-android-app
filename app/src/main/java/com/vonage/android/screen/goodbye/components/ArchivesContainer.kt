package com.vonage.android.screen.goodbye.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.R
import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveStatus.AVAILABLE
import com.vonage.android.archiving.ArchiveStatus.FAILED
import com.vonage.android.archiving.ArchiveStatus.PENDING
import com.vonage.android.archiving.ui.ArchiveListStyle
import com.vonage.android.archiving.ui.ArchivesList
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.screen.goodbye.GoodbyeScreenActions
import com.vonage.android.screen.goodbye.GoodbyeScreenUiState
import kotlinx.collections.immutable.persistentListOf
import java.text.SimpleDateFormat

private val dateFormat = SimpleDateFormat("dd MMM y HH:mm", Locale.current.platformLocale)

@Composable
fun ArchivesContainer(
    uiState: GoodbyeScreenUiState,
    actions: GoodbyeScreenActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier
                .padding(bottom = VonageVideoTheme.dimens.paddingSmall),
            text = stringResource(R.string.recording_title),
            style = VonageVideoTheme.typography.heading4,
            color = VonageVideoTheme.colors.textSecondary,
        )
        when (uiState) {
            is GoodbyeScreenUiState.Content -> {
                ArchivesList(
                    archives = uiState.archives,
                    onDownloadArchive = actions.onDownloadArchive,
                    archiveListStyle = ArchiveListStyle(
                        dateFormat = dateFormat,
                        emptyLabel = stringResource(R.string.recording_empty_title),
                    ),
                )
            }

            is GoodbyeScreenUiState.Idle -> null
        }
    }
}

@PreviewLightDark
@Composable
internal fun ArchiveListPreview() {
    VonageVideoTheme {
        Box(
            modifier = Modifier.background(VonageVideoTheme.colors.surface)
        ) {
            ArchivesContainer(
                actions = GoodbyeScreenActions(),
                uiState = GoodbyeScreenUiState.Content(
                    archives = persistentListOf(
                        Archive(
                            id = "1",
                            name = "Recording Available",
                            url = "url",
                            status = AVAILABLE,
                            createdAt = 1231,
                            duration = 123,
                            size = 13,
                        ),
                        Archive(
                            id = "2",
                            name = "Recording Pending",
                            url = "url",
                            status = PENDING,
                            createdAt = 1231,
                            duration = 123,
                            size = 13,
                        ),
                        Archive(
                            id = "3",
                            name = "Recording Failed",
                            url = "url",
                            status = FAILED,
                            createdAt = 1231,
                            duration = 123,
                            size = 13,
                        ),
                    )
                ),
            )
        }
    }
}
