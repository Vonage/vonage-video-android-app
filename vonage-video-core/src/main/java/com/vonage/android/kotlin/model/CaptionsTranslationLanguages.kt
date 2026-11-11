package com.vonage.android.kotlin.model

/**
 * BCP-47 codes accepted by [SessionFacade.setCaptionsTranslationLanguage].
 *
 * Same list as the web reference app: the SDK also supports `eu-ES`, `gl-ES`, `zu-ZA`, `en-AB`
 * and `en-WL`, which are left out for cross-platform parity.
 */
val CAPTIONS_TRANSLATION_LANGUAGES: List<String> = listOf(
    "af-ZA", "ar-AE", "ar-SA", "ca-ES", "zh-HK", "zh-CN", "zh-TW", "hr-HR", "cs-CZ", "da-DK",
    "nl-NL", "en-AU", "en-GB", "en-IN", "en-IE", "en-NZ", "en-ZA", "en-US", "fa-IR", "fi-FI",
    "fr-FR", "fr-CA", "de-DE", "de-CH", "el-GR", "he-IL", "hi-IN", "id-ID", "it-IT", "ja-JP",
    "ko-KR", "lv-LV", "ms-MY", "no-NO", "pl-PL", "pt-PT", "pt-BR", "ro-RO", "ru-RU", "sr-RS",
    "sk-SK", "so-SO", "es-ES", "es-US", "sv-SE", "tl-PH", "th-TH", "uk-UA", "vi-VN",
)
