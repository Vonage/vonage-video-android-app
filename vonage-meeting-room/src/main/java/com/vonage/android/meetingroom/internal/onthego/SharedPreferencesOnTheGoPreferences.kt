package com.vonage.android.meetingroom.internal.onthego

import android.content.Context
import android.content.SharedPreferences

/**
 * [OnTheGoPreferences] backed by [SharedPreferences].
 *
 * The meeting room and the settings screen can each hold an instance. Android
 * delivers change callbacks to every listener on the same preferences file, on
 * the main thread, before [SharedPreferences.Editor.apply] returns when the
 * edit itself happens on the main thread.
 */
internal class SharedPreferencesOnTheGoPreferences(
    context: Context,
) : OnTheGoPreferences {
    private val preferences = context.applicationContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
    private val listeners = linkedSetOf<(Boolean) -> Unit>()
    private var closed = false

    private val systemListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == SUGGEST_WHEN_MOVING_KEY) {
            val value = suggestWhenMoving
            listeners.toList().forEach { listener -> listener(value) }
        }
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(systemListener)
    }

    override var suggestWhenMoving: Boolean
        get() = preferences.getBoolean(SUGGEST_WHEN_MOVING_KEY, true)
        set(value) {
            preferences.edit().putBoolean(SUGGEST_WHEN_MOVING_KEY, value).apply()
        }

    override fun addListener(listener: (Boolean) -> Unit) {
        listeners += listener
    }

    override fun removeListener(listener: (Boolean) -> Unit) {
        listeners -= listener
    }

    override fun close() {
        if (closed) return
        closed = true
        preferences.unregisterOnSharedPreferenceChangeListener(systemListener)
        listeners.clear()
    }

    companion object {
        const val FILE_NAME = "on_the_go"
        const val SUGGEST_WHEN_MOVING_KEY = "onTheGo.suggestWhenMoving"
    }
}
