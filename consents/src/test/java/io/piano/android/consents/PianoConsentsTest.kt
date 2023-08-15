package io.piano.android.consents

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.squareup.moshi.JsonAdapter
import io.piano.android.consents.models.ConsentConfiguration
import io.piano.android.consents.models.ConsentMode
import io.piano.android.consents.models.Product
import io.piano.android.consents.models.Purpose
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PianoConsentsTest {
    private val prefsStorage: PrefsStorage = mock()
    private val purposesAdapter: JsonAdapter<Map<Product, Purpose>> = mock() {
        on { toJson(any()) } doReturn DUMMY_STRING
    }
    private val consentModesAdapter: JsonAdapter<Map<Purpose, ConsentMode>> = mock() {
        on { toJson(any()) } doReturn DUMMY_STRING
    }

    private fun initPianoConsents(consentConfiguration: ConsentConfiguration) =
        PianoConsents(consentConfiguration, prefsStorage, purposesAdapter, consentModesAdapter)

    @Test
    fun consentNotRequired() {
        val pianoConsents = initPianoConsents(ConsentConfiguration(requireConsent = false))
        verify(prefsStorage).consents = ""
        verify(prefsStorage).purposes = ""
        verify(purposesAdapter, never()).fromJson(any<String>())
        verify(consentModesAdapter, never()).fromJson(any<String>())
        assertEquals(emptyMap(), pianoConsents.productsToPurposesMapping)
        assertEquals(emptyMap(), pianoConsents.consents)
        assertFailsWith<IllegalStateException> {
            pianoConsents.set(Purpose.CONTENT_PERSONALISATION, ConsentMode.OPT_OUT)
        }
        assertFailsWith<IllegalStateException> { pianoConsents.setAll(ConsentMode.OPT_OUT) }
        verify(purposesAdapter, never()).toJson(any())
        verify(consentModesAdapter, never()).toJson(any())
    }

    @Test
    fun emptyInitialConsentWithDefaultPurposes() {
        doReturn("").`when`(prefsStorage).consents
        doReturn("").`when`(prefsStorage).purposes
        val customPurpose = Purpose(DUMMY_STRING)
        val pianoConsents = initPianoConsents(
            ConsentConfiguration(
                requireConsent = true,
                defaultPurposes = mapOf(
                    Product.ID to customPurpose
                )
            )
        )
        verify(purposesAdapter, never()).fromJson(any<String>())
        verify(consentModesAdapter, never()).fromJson(any<String>())

        pianoConsents.productsToPurposesMapping.apply {
            assertEquals(PianoConsents.DEFAULT_PURPOSES_MAP.size, size)
            val changed = filterValues { it == customPurpose }
            assertEquals(1, changed.size)
            assertEquals(Product.ID, changed.keys.first())
        }
        pianoConsents.consents.filterKeys { it.alias == DUMMY_STRING }.apply {
            assertEquals(1, size)
            assertContentEquals(listOf(Product.ID), values.first().products)
        }

        assertFailsWith<IllegalArgumentException> { pianoConsents.set(customPurpose, ConsentMode.NOT_ACQUIRED) }
        assertFailsWith<IllegalArgumentException> { pianoConsents.setAll(ConsentMode.NOT_ACQUIRED) }

        pianoConsents.set(customPurpose, ConsentMode.OPT_OUT)
        assertEquals(1, pianoConsents.consents.filterValues { it.mode == ConsentMode.OPT_OUT }.size)
        verify(purposesAdapter).toJson(any())
        verify(consentModesAdapter).toJson(any())
    }

    @Test
    fun notEmptyInitialConsent() {
        doReturn(DUMMY_STRING).`when`(prefsStorage).consents
        doReturn("").`when`(prefsStorage).purposes
        doReturn(mapOf(Purpose.AUDIENCE_MEASUREMENT to ConsentMode.OPT_OUT)).`when`(consentModesAdapter)
            .fromJson(DUMMY_STRING)
        val customPurpose = Purpose(DUMMY_STRING)
        val pianoConsents = initPianoConsents(ConsentConfiguration(requireConsent = true))
        verify(purposesAdapter, never()).fromJson(any<String>())
        verify(consentModesAdapter).fromJson(any<String>())
        assertEquals(PianoConsents.DEFAULT_PURPOSES_MAP, pianoConsents.productsToPurposesMapping)

        pianoConsents.consents.filterValues { it.mode == ConsentMode.OPT_OUT }.apply {
            assertEquals(1, size)
            assertEquals(Purpose.AUDIENCE_MEASUREMENT, keys.first())
        }
        assertTrue {
            pianoConsents.consents.filterKeys { it != Purpose.AUDIENCE_MEASUREMENT }
                .all { it.value.mode == ConsentMode.OPT_IN }
        }

        assertFailsWith<IllegalArgumentException> {
            pianoConsents.set(
                Purpose.CONTENT_PERSONALISATION,
                ConsentMode.NOT_ACQUIRED
            )
        }
        assertFailsWith<IllegalArgumentException> { pianoConsents.setAll(ConsentMode.NOT_ACQUIRED) }
        assertFailsWith<IllegalArgumentException> { pianoConsents.set(customPurpose, ConsentMode.OPT_OUT) }

        pianoConsents.set(customPurpose, ConsentMode.ESSENTIAL, Product.ID)
        pianoConsents.productsToPurposesMapping.filterValues { it == customPurpose }.apply {
            assertEquals(1, size)
            assertEquals(Product.ID, keys.first())
        }
        assertEquals(1, pianoConsents.consents.filterValues { it.mode == ConsentMode.ESSENTIAL }.size)

        pianoConsents.setAll(ConsentMode.OPT_IN)
        assertTrue {
            pianoConsents.consents.values.all { it.mode == ConsentMode.OPT_IN }
        }

        verify(purposesAdapter, times(2)).toJson(any())
        verify(consentModesAdapter, times(2)).toJson(any())
    }

    companion object {
        private const val DUMMY_STRING = "DUMMY"
    }
}
