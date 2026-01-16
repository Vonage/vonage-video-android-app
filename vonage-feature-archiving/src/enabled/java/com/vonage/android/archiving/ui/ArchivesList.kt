package com.vonage.android.archiving.ui

import android.text.format.DateUtils.formatElapsedTime
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.style.TextOverflow
import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveListStyle
import com.vonage.android.archiving.ArchiveStatus
import com.vonage.android.compose.theme.VonageVideoTheme
import com.vonage.android.compose.vivid.icons.VividIcons
import com.vonage.android.compose.vivid.icons.solid.Download
import com.vonage.android.compose.vivid.icons.solid.Error
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ArchivesList(
    archives: ImmutableList<Archive>,
    onDownloadArchive: (Archive) -> Unit,
    archiveListStyle: ArchiveListStyle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(VonageVideoTheme.dimens.spaceSmall)
    ) {
        if (archives.isEmpty()) {
            ArchiveEmptyRow(
                style = archiveListStyle,
            )
        } else {
            archives.forEach { archive ->
                ArchiveRow(
                    archive = archive,
                    onDownloadArchive = onDownloadArchive,
                    style = archiveListStyle,
                )
            }
        }
    }
}

@Composable
private fun ArchiveEmptyRow(
    style: ArchiveListStyle,
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
            text = style.emptyLabel,
            style = VonageVideoTheme.typography.bodyExtended,
            color = VonageVideoTheme.colors.onSurface,
        )
    }
}

@Composable
private fun ArchiveRow(
    archive: Archive,
    style: ArchiveListStyle,
    onDownloadArchive: (Archive) -> Unit,
) {
    val title = remember(archive) {
        when (archive.status) {
            ArchiveStatus.AVAILABLE -> {
                listOf(
                    formatElapsedTime(archive.duration.toLong()),
                    bytesToHumanReadableSize(archive.size.toFloat()),
                    style.dateFormat.format(archive.createdAt),
                ).joinToString(separator = " • ")
            }

            ArchiveStatus.PENDING,
            ArchiveStatus.FAILED -> null
        }
    }
    Row(
        modifier = Modifier
            .clickable(onClick = { onDownloadArchive(archive) })
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
            title?.let {
                Text(
                    text = it,
                    style = VonageVideoTheme.typography.bodyBase,
                    color = VonageVideoTheme.colors.textSecondary,
                )
            }
        }

        when (archive.status) {
            ArchiveStatus.AVAILABLE -> {
                Icon(
                    modifier = Modifier
                        .size(VonageVideoTheme.dimens.iconSizeDefault),
                    imageVector = VividIcons.Solid.Download,
                    contentDescription = null,
                    tint = VonageVideoTheme.colors.primary,
                )
            }

            ArchiveStatus.PENDING -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(VonageVideoTheme.dimens.iconSizeDefault),
                )
            }

            ArchiveStatus.FAILED -> {
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

@Suppress("MagicNumber")
private fun bytesToHumanReadableSize(bytes: Float) = when {
    bytes >= 1 shl 30 -> "%.1f GB".format(bytes / (1 shl 30))
    bytes >= 1 shl 20 -> "%.1f MB".format(bytes / (1 shl 20))
    bytes >= 1 shl 10 -> "%.0f kB".format(bytes / (1 shl 10))
    else -> "$bytes bytes"
}
