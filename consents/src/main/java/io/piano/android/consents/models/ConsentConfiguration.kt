package io.piano.android.consents.models

/**
 * Configuration for consents
 * @property requireConsent specifies enable consent or not
 * @property defaultPurposes default mapping for [Product] by [Purpose]. Will be used only if there is no any saved consents
 */
class ConsentConfiguration @JvmOverloads constructor(
    val requireConsent: Boolean = false,
    val defaultPurposes: Map<Product, Purpose>? = null,
)
