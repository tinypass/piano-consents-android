package io.piano.android.consents.models

/**
 * Products list. Mot all of them are available for mobile apps
 */
enum class Product(val id: Int, val alias: String) {
    PA(0, "PA"),
    DMP(1, "DMP"),
    COMPOSER(2, "COMPOSER"),
    ID(3, "ID"),
    VX(4, "VX"),
    ESP(5, "ESP"),
    SOCIAL_FLOW(6, "SOCIAL_FLOW"),
}
