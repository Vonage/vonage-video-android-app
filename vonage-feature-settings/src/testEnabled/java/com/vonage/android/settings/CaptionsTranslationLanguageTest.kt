package com.vonage.android.settings

import com.vonage.android.kotlin.model.CAPTIONS_TRANSLATION_LANGUAGES
import org.junit.jupiter.api.Test
import java.util.Locale
import kotlin.test.assertEquals

class CaptionsTranslationLanguageTest {

    private val languages = captionsTranslationLanguages(Locale.ENGLISH)

    @Test
    fun `captionsTranslationLanguages offers every supported code`() {
        assertEquals(CAPTIONS_TRANSLATION_LANGUAGES.toSet(), languages.map { it.code }.toSet())
    }

    @Test
    fun `captionsTranslationLanguages names languages in the display locale`() {
        assertEquals("Spanish (Spain)", languages.first { it.code == "es-ES" }.displayName)
    }

    @Test
    fun `captionsTranslationLanguages sorts by display name`() {
        val names = languages.map { it.displayName }
        assertEquals(names.sorted(), names)
    }
}
