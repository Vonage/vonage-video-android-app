package com.vonage.android.util

import com.vonage.android.BuildConfig

/**
 * [BuildConfig.BASE_API_URL] normalised for building link URLs.
 *
 * The configured value may or may not carry a trailing slash, and both the navigation
 * deep-link patterns and the shared room links must agree with the App Link filter in
 * `AndroidManifest.xml` — a doubled `//room/...` path matches none of them. Retrofit
 * keeps using the raw value, since its base URL requires the trailing slash.
 */
internal val DEEP_LINK_BASE_URL: String = BuildConfig.BASE_API_URL.trimEnd('/')
