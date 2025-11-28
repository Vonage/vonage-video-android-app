package com.vonage.android.screen.goodbye.components

import android.text.format.DateUtils.formatElapsedTime
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
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

val dateFormat = SimpleDateFormat("dd MMM y HH:mm", Locale.current.platformLocale)

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
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall)
    ) {
        if (archives.isEmpty()) {
            ArchiveEmptyRow()
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
private fun ArchiveEmptyRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(VonageVideoTheme.dimens.paddingLarge)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault)
    ) {
        RecordingIcon()
        Text(
            text = stringResource(R.string.recording_empty_title),
            style = VonageVideoTheme.typography.bodyExtended,
            color = VonageVideoTheme.colors.onSurface,
        )
    }
}

@Composable
private fun ArchiveRow(
    archive: Archive,
    actions: GoodbyeScreenActions,
) {
    val title = remember(archive) {
        listOf(
            formatElapsedTime(archive.duration.toLong()),
            bytesToHumanReadableSize(archive.size.toFloat()),
            dateFormat.format(archive.createdAt),
        ).joinToString(separator = " • ")
    }
    Row(
        modifier = Modifier
            .clickable(onClick = { actions.onDownloadArchive(archive) })
            .fillMaxWidth()
            .padding(VonageVideoTheme.dimens.paddingSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceDefault),
    ) {
        RecordingIcon()
        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.paddingSmall),
        ) {
            Text(
                modifier = Modifier
                    .padding(end = VonageVideoTheme.dimens.paddingSmall),
                text = archive.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = VonageVideoTheme.typography.heading3,
                color = VonageVideoTheme.colors.onSurface,
            )
            Text(
                text = title,
                style = VonageVideoTheme.typography.bodyBase,
                color = VonageVideoTheme.colors.textSecondary,
            )
        }

        when (archive.status) {
            AVAILABLE -> {
                Icon(
                    modifier = Modifier
                        .size(VonageVideoTheme.dimens.iconSizeDefault),
                    imageVector = VividIcons.Solid.Download,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.primary,
                )
            }

            PENDING -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(VonageVideoTheme.dimens.iconSizeDefault),
                )
            }

            FAILED -> {
                Icon(
                    modifier = Modifier
                        .size(VonageVideoTheme.dimens.iconSizeDefault),
                    imageVector = VividIcons.Solid.Error,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.error,
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

@Suppress("MagicNumber")
private fun bytesToHumanReadableSize(bytes: Float) = when {
    bytes >= 1 shl 30 -> "%.1f GB".format(bytes / (1 shl 30))
    bytes >= 1 shl 20 -> "%.1f MB".format(bytes / (1 shl 20))
    bytes >= 1 shl 10 -> "%.0f kB".format(bytes / (1 shl 10))
    else -> "$bytes bytes"
}
