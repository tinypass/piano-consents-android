package io.piano.android.consents.models

/**
 * Consent modes
 */
enum class ConsentMode(val id: Int, val alias: String) {
    /**
     * All data will be used
     */
    OPT_IN(0, "opt-in"),

    /**
     * Only essential and mandatory data will be used
     */
    ESSENTIAL(1, "essential"),

    /**
     * Only mandatory data will be used
     */
    OPT_OUT(2, "opt-out"),

    /**
     * Custom mode
     */
    CUSTOM(3, "custom"),

    /**
     * Consent was not acquired. You can't set this intenal mode
     */
    NOT_ACQUIRED(4, "not-acquired"),
}
