package com.vonage.android.settings.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.DropdownItem
import com.vonage.android.compose.components.DropdownSelector
import com.vonage.android.kotlin.model.CaptureResolution
import com.vonage.android.settings.R

private const val AUTO_LABEL = "Auto (device-optimal)"
private const val AUTO_DESCRIPTION = "Selects resolution based on device memory"

@Composable
internal fun ResolutionSelector(
    selected: CaptureResolution?,
    onSelectionChange: (CaptureResolution?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember {
        buildList {
            add(
                DropdownItem(
                    value = null,
                    label = AUTO_LABEL,
                    description = AUTO_DESCRIPTION,
                ),
            )
            CaptureResolution.entries.forEach { resolution ->
                add(
                    DropdownItem(
                        value = resolution,
                        label = resolution.label,
                        description = resolution.description,
                    ),
                )
            }
        }
    }

    DropdownSelector(
        title = stringResource(R.string.settings_resolution_title),
        selectedLabel = selected?.label ?: AUTO_LABEL,
        dropdownLabel = stringResource(R.string.settings_resolution_label),
        items = items,
        onItemSelected = onSelectionChange,
        modifier = modifier,
        note = stringResource(R.string.settings_resolution_note),
    )
}
