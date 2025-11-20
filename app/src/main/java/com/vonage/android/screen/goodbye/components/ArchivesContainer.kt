package com.vonage.android.screen.goodbye.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.vonage.android.R
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Download
import com.vonage.android.compose.vivid.icons.solid.Error
import com.vonage.android.data.Archive
import com.vonage.android.data.ArchiveStatus.AVAILABLE
import com.vonage.android.data.ArchiveStatus.FAILED
import com.vonage.android.data.ArchiveStatus.PENDING
import com.vonage.android.screen.goodbye.GoodbyeScreenActions
import com.vonage.android.screen.goodbye.GoodbyeScreenUiState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.text.SimpleDateFormat

val dateFormat = SimpleDateFormat("EEE, dd MMM hh:mm a", Locale.current.platformLocale)

@Composable
fun ArchivesContainer(
    uiState: GoodbyeScreenUiState,
    actions: GoodbyeScreenActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier
                .padding(bottom = 8.dp),
            text = stringResource(R.string.recording_title),
            style = VonageVideoTheme.typography.heading1,
            color = VonageVideoTheme.colors.onSurface,
            textAlign = TextAlign.Center,
        )
        when (uiState) {
            is GoodbyeScreenUiState.Content -> {
                ArchivesList(
                    archives = uiState.archives,
                    actions = actions,
                )
            }

            is GoodbyeScreenUiState.Idle -> null
        }
    }
}

@Composable
private fun ArchivesList(
    archives: ImmutableList<Archive>,
    actions: GoodbyeScreenActions,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (archives.isEmpty()) {
            Text(stringResource(R.string.recording_empty_title))
        } else {
            archives.forEach { archive ->
                ArchiveRow(
                    archive = archive,
                    actions = actions,
                )
            }
        }
    }
}

@Composable
private fun ArchiveRow(
    archive: Archive,
    actions: GoodbyeScreenActions,
) {
    Row(
        modifier = Modifier
            .clickable(onClick = { actions.onDownloadArchive(archive) })
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Column(
            modifier = Modifier
                .weight(1f),
        ) {
            Text(
                modifier = Modifier
                    .padding(end = 8.dp),
                text = archive.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.onSurface,
            )
            Text(
                text = dateFormat.format(archive.createdAt),
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.textSecondary,
            )
        }

        when (archive.status) {
            AVAILABLE -> {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = VividIcons.Solid.Download,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.onSurface,
                )
            }

            PENDING -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                )
            }

            FAILED -> {
                Icon(
                    modifier = Modifier.size(24.dp),
                    imageVector = VividIcons.Solid.Error,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.onSurface,
                )
            }
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
            ArchivesList(
                actions = GoodbyeScreenActions(),
                archives = persistentListOf(
                    Archive(
                        id = "1",
                        name = "Recording Available",
                        url = "url",
                        status = AVAILABLE,
                        createdAt = 1231,
                    ),
                    Archive(
                        id = "2",
                        name = "Recording Pending",
                        url = "url",
                        status = PENDING,
                        createdAt = 1231,
                    ),
                    Archive(
                        id = "3",
                        name = "Recording Failed",
                        url = "url",
                        status = FAILED,
                        createdAt = 1231,
                    ),
                )
            )
        }
    }
}
