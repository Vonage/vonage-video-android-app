package com.vonage.android.archiving.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.vonage.android.archiving.Archive
import com.vonage.android.archiving.ArchiveListStyle
import kotlinx.collections.immutable.ImmutableList

@Suppress("UnusedParameter", "EmptyFunctionBlock")
@Composable
fun ArchivesContainer(
    archives: ImmutableList<Archive>,
    onDownloadArchive: (Archive) -> Unit,
    style: ArchiveListStyle,
    modifier: Modifier = Modifier,
) {

}
