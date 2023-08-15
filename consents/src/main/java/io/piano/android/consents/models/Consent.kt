package io.piano.android.consents.models

import com.squareup.moshi.JsonClass

/**
 * Full consent data
 * @property mode chosen [ConsentMode]
 * @property products List of products for [mode]
 */
@JsonClass(generateAdapter = true)
class Consent(
    val mode: ConsentMode,
    val products: List<Product>,
)
