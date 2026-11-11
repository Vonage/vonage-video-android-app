package com.vonage.android.settings.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vonage.android.compose.components.DropdownItem
import com.vonage.android.compose.components.DropdownSelector
import com.vonage.android.settings.CaptionsTranslationLanguage
import com.vonage.android.settings.R
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/** Picks the target language for received captions; `null` keeps the spoken language. */
@Composable
internal fun CaptionsTranslationLanguageSelector(
    languages: ImmutableList<CaptionsTranslationLanguage>,
    selected: String?,
    onSelectionChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val originalLabel = stringResource(R.string.settings_captions_translation_original)
    val items = remember(languages, originalLabel) {
        val languageItems = languages.map { DropdownItem<String?>(value = it.code, label = it.displayName) }
        (listOf(DropdownItem<String?>(value = null, label = originalLabel)) + languageItems).toImmutableList()
    }

    DropdownSelector(
        title = stringResource(R.string.settings_captions_translation_title),
        selectedLabel = items.firstOrNull { it.value == selected }?.label ?: originalLabel,
        dropdownLabel = stringResource(R.string.settings_captions_translation_label),
        items = items,
        onSelectItem = onSelectionChange,
        modifier = modifier,
        selectedDescription = stringResource(R.string.settings_captions_translation_description),
    )
}
