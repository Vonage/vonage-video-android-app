package com.vonage.android.util.ext

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.firstOrNull

suspend fun <T> DataStore<Preferences>.get(key: Preferences.Key<T>): T? =
    data.firstOrNull()?.get(key)
