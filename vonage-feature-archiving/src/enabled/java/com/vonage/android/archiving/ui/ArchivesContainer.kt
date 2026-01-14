package com.vonage.android.archiving.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveId
import com.vonage.android.archiving.ArchiveListStyle
import com.vonage.android.archiving.ArchiveStatus.AVAILABLE
import com.vonage.android.archiving.ArchiveStatus.FAILED
import com.vonage.android.archiving.ArchiveStatus.PENDING
import com.vonage.android.compose.theme.VonageVideoTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import java.text.SimpleDateFormat

@Composable
fun ArchivesContainer(
    archives: ImmutableList<Archive>,
    onDownloadArchive: (Archive) -> Unit,
    style: ArchiveListStyle,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier
                .padding(bottom = VonageVideoTheme.dimens.paddingSmall),
            text = style.containerTitle,
            style = VonageVideoTheme.typography.heading4,
            color = VonageVideoTheme.colors.textSecondary,
        )
        ArchivesList(
            archives = archives,
            onDownloadArchive = onDownloadArchive,
            archiveListStyle = style,
        )
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
                onDownloadArchive = {},
                style = ArchiveListStyle(
                    dateFormat = SimpleDateFormat("dd MMM y HH:mm", Locale.current.platformLocale),
                    containerTitle = "Title",
                    emptyLabel = "Empty"
                ),
                archives = persistentListOf(
                    Archive(
                        id = ArchiveId("1"),
                        name = "Recording Available",
                        url = "url",
                        status = AVAILABLE,
                        createdAt = 1231,
                        duration = 123,
                        size = 13,
                    ),
                    Archive(
                        id = ArchiveId("2"),
                        name = "Recording Pending",
                        url = "url",
                        status = PENDING,
                        createdAt = 1231,
                        duration = 123,
                        size = 13,
                    ),
                    Archive(
                        id = ArchiveId("3"),
                        name = "Recording Failed",
                        url = "url",
                        status = FAILED,
                        createdAt = 1231,
                        duration = 123,
                        size = 13,
                    ),
                )
            )
        }
    }
}
