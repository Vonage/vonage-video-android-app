package com.vonage.android.settings.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.DropdownItem
import com.vonage.android.compose.components.DropdownSelector
import com.vonage.android.kotlin.model.DegradationPreference
import com.vonage.android.settings.R

@Composable
internal fun DegradationPreferenceSelector(
    selected: DegradationPreference,
    onSelectionChange: (DegradationPreference) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = remember {
        DegradationPreference.entries.map {
            DropdownItem(value = it, label = it.label, description = it.description)
        }
    }

    DropdownSelector(
        title = stringResource(R.string.settings_degradation_title),
        selectedLabel = selected.label,
        dropdownLabel = stringResource(R.string.settings_degradation_preset_label),
        items = items,
        onItemSelected = onSelectionChange,
        modifier = modifier,
        selectedDescription = selected.description,
    )
}
