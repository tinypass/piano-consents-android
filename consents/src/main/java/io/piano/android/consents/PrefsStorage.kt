package io.piano.android.consents

import android.content.Context
import android.content.SharedPreferences

internal class PrefsStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var consents: String
        get() = prefs.getString(KEY_CONSENTS, null).orEmpty()
        set(value) {
            prefs.edit().putString(KEY_CONSENTS, value).apply()
        }

    var purposes: String
        get() = prefs.getString(KEY_PURPOSES, null).orEmpty()
        set(value) {
            prefs.edit().putString(KEY_PURPOSES, value).apply()
        }

    companion object {
        private const val PREFS_NAME = "io.piano.android.consents"
        internal const val KEY_CONSENTS = "consents"
        internal const val KEY_PURPOSES = "purposes"
    }
}
