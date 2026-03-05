package com.vonage.android.settings.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.kotlin.model.CaptureFrameRate
import com.vonage.android.settings.R

@Composable
internal fun FrameRateSelector(
    selected: CaptureFrameRate,
    onSelectionChange: (CaptureFrameRate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember {
        CaptureFrameRate.entries.map { DropdownItem(value = it, label = it.label) }
    }

    DropdownSelector(
        title = stringResource(R.string.settings_frame_rate_title),
        selectedLabel = selected.label,
        dropdownLabel = stringResource(R.string.settings_frame_rate_label),
        items = items,
        onItemSelected = onSelectionChange,
        modifier = modifier,
        note = stringResource(R.string.settings_frame_rate_note),
    )
}
