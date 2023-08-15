package io.piano.android.consents.models

/**
 * Purpose class. You can create your custom purpose or use one of predefined,
 * like [ADVERTISING], [AUDIENCE_MEASUREMENT], [CONTENT_PERSONALISATION] or [PERSONAL_RELATIONSHIP]
 */
@JvmInline
value class Purpose private constructor(val alias: String) : Comparable<Purpose> {

    override fun compareTo(other: Purpose): Int {
        return alias.compareTo(other.alias)
    }

    companion object {
        internal const val ALIAS_AUDIENCE_MEASUREMENT = "AM"
        internal const val ALIAS_CONTENT_PERSONALISATION = "CP"
        internal const val ALIAS_ADVERTISING = "AD"
        internal const val ALIAS_PERSONAL_RELATIONSHIP = "PR"

        @JvmStatic
        private val RESERVED = arrayOf(
            ALIAS_AUDIENCE_MEASUREMENT,
            ALIAS_CONTENT_PERSONALISATION,
            ALIAS_ADVERTISING,
            ALIAS_PERSONAL_RELATIONSHIP,
            "DL"
        )

        @JvmStatic
        val AUDIENCE_MEASUREMENT = Purpose(ALIAS_AUDIENCE_MEASUREMENT)

        @JvmStatic
        val CONTENT_PERSONALISATION = Purpose(ALIAS_CONTENT_PERSONALISATION)

        @JvmStatic
        val ADVERTISING = Purpose(ALIAS_ADVERTISING)

        @JvmStatic
        val PERSONAL_RELATIONSHIP = Purpose(ALIAS_PERSONAL_RELATIONSHIP)

        @JvmName("create")
        @JvmStatic
        operator fun invoke(alias: String): Purpose {
            require(alias.isNotBlank() && alias.length <= 32 && alias.uppercase() !in RESERVED) {
                "Invalid custom purpose"
            }
            return Purpose(alias)
        }
    }
}
