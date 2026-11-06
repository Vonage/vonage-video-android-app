package com.vonage.android.settings.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.core.os.ConfigurationCompat
import com.vonage.android.compose.components.DropdownItem
import com.vonage.android.compose.components.DropdownSelector
import com.vonage.android.kotlin.model.CAPTIONS_TRANSLATION_LANGUAGES
import com.vonage.android.settings.R
import java.text.Collator
import java.util.Locale
import kotlinx.collections.immutable.toImmutableList

/** Picks the target language for received captions; `null` keeps the spoken language. */
@Composable
internal fun CaptionsTranslationLanguageSelector(
    selected: String?,
    onSelectionChange: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val originalLabel = stringResource(R.string.settings_captions_translation_original)
    val displayLocale = ConfigurationCompat.getLocales(LocalConfiguration.current)[0] ?: Locale.getDefault()
    val items = remember(originalLabel, displayLocale) {
        val collator = Collator.getInstance(displayLocale)
        val languages = CAPTIONS_TRANSLATION_LANGUAGES
            .map { code -> DropdownItem<String?>(value = code, label = code.displayName(displayLocale)) }
            .sortedWith { a, b -> collator.compare(a.label, b.label) }
        (listOf(DropdownItem<String?>(value = null, label = originalLabel)) + languages).toImmutableList()
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

private fun String.displayName(displayLocale: Locale): String =
    Locale.forLanguageTag(this).getDisplayName(displayLocale)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(displayLocale) else it.toString() }
