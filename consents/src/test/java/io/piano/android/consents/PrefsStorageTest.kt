package io.piano.android.consents

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class PrefsStorageTest {
    private val prefsEditor: SharedPreferences.Editor = mock {
        on { putString(any(), any()) } doReturn mock
    }
    private val prefs: SharedPreferences = mock {
        on { edit() } doReturn prefsEditor
    }
    private val context: Context = mock {
        on { getSharedPreferences(any(), any()) } doReturn prefs
    }
    private val prefsStorage = PrefsStorage(context)

    @Test
    fun getConsents() {
        doReturn(DUMMY_STRING).`when`(prefs).getString(PrefsStorage.KEY_CONSENTS, null)
        assertEquals(DUMMY_STRING, prefsStorage.consents)
        verify(prefs).getString(PrefsStorage.KEY_CONSENTS, null)
    }

    @Test
    fun setConsents() {
        prefsStorage.consents = DUMMY_STRING
        verify(prefsEditor).putString(PrefsStorage.KEY_CONSENTS, DUMMY_STRING)
    }

    @Test
    fun getPurposes() {
        doReturn(DUMMY_STRING).`when`(prefs).getString(PrefsStorage.KEY_PURPOSES, null)
        assertEquals(DUMMY_STRING, prefsStorage.purposes)
        verify(prefs).getString(PrefsStorage.KEY_PURPOSES, null)
    }

    @Test
    fun setPurposes() {
        prefsStorage.purposes = DUMMY_STRING
        verify(prefsEditor).putString(PrefsStorage.KEY_PURPOSES, DUMMY_STRING)
    }

    companion object {
        private const val DUMMY_STRING = "DUMMY"
    }
}
