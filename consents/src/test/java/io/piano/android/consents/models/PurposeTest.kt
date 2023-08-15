package io.piano.android.consents.models

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PurposeTest {
    @Test
    fun createValidCustomPurpose() {
        val purpose = Purpose(DUMMY_STRING)
        assertEquals(DUMMY_STRING, purpose.alias)
    }

    @Test
    fun createReservedCustomPurpose() {
        assertFailsWith<IllegalArgumentException> {
            Purpose(Purpose.ALIAS_ADVERTISING)
        }
    }

    @Test
    fun createInvalidCustomPurpose() {
        assertFailsWith<IllegalArgumentException> {
            Purpose("")
        }
        assertFailsWith<IllegalArgumentException> {
            Purpose("  ")
        }
        assertFailsWith<IllegalArgumentException> {
            Purpose("t".repeat(40))
        }
    }

    companion object {
        private const val DUMMY_STRING = "DUMMY"
    }
}
