package com.vonage.android.settings

import com.vonage.android.kotlin.model.CAPTIONS_TRANSLATION_LANGUAGES
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.text.Collator
import java.util.Locale

/** A target language for received captions: its BCP-47 [code] and its [displayName]. */
data class CaptionsTranslationLanguage(
    val code: String,
    val displayName: String,
)

/** Every supported translation language, named and sorted alphabetically in [displayLocale]. */
fun captionsTranslationLanguages(displayLocale: Locale): ImmutableList<CaptionsTranslationLanguage> {
    val collator = Collator.getInstance(displayLocale)
    return CAPTIONS_TRANSLATION_LANGUAGES
        .map { code -> CaptionsTranslationLanguage(code = code, displayName = code.displayName(displayLocale)) }
        .sortedWith { a, b -> collator.compare(a.displayName, b.displayName) }
        .toImmutableList()
}

private fun String.displayName(displayLocale: Locale): String =
    Locale.forLanguageTag(this).getDisplayName(displayLocale)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(displayLocale) else it.toString() }
