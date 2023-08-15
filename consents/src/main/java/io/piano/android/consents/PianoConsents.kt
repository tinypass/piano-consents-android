package io.piano.android.consents

import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.piano.android.consents.models.Consent
import io.piano.android.consents.models.ConsentConfiguration
import io.piano.android.consents.models.ConsentMode
import io.piano.android.consents.models.Product
import io.piano.android.consents.models.Purpose
import java.util.Collections

/**
 * Storage for user consent
 */
class PianoConsents internal constructor(
    val consentConfiguration: ConsentConfiguration,
    private val prefsStorage: PrefsStorage,
    private val purposesAdapter: JsonAdapter<Map<Product, Purpose>>,
    private val consentModesAdapter: JsonAdapter<Map<Purpose, ConsentMode>>,
) {
    private val purposesByProduct: MutableMap<Product, Purpose> = if (consentConfiguration.requireConsent) {
        DEFAULT_PURPOSES_MAP.toMutableMap()
    } else {
        mutableMapOf()
    }
    private val changedConsents = mutableMapOf<Purpose, ConsentMode>()

    private val productsByPurpose
        get() = purposesByProduct.entries.groupBy({ it.value }, { it.key })

    init {
        if (!consentConfiguration.requireConsent) {
            prefsStorage.consents = ""
            prefsStorage.purposes = ""
        } else {
            if (prefsStorage.consents.isEmpty()) {
                updateDefaultPurposes()
            } else {
                if (prefsStorage.purposes.isNotEmpty()) {
                    purposesAdapter.fromJson(prefsStorage.purposes)?.let {
                        purposesByProduct.clear()
                        purposesByProduct.putAll(it)
                    }
                }
                consentModesAdapter.fromJson(prefsStorage.consents)?.let {
                    changedConsents.putAll(it)
                }
            }
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun updateDefaultPurposes() {
        if (consentConfiguration.defaultPurposes != null) {
            purposesByProduct.putAll(consentConfiguration.defaultPurposes)
        }
    }

    private fun save() = prefsStorage.apply {
        purposes = if (purposesByProduct.equals(DEFAULT_PURPOSES_MAP)) "" else purposesAdapter.toJson(purposesByProduct)
        consents = if (changedConsents.isEmpty()) "" else consentModesAdapter.toJson(changedConsents)
    }

    /**
     * Returns read only copy of mapping [Product] to [Purpose]
     */
    @Suppress("unused") // Public API.
    val productsToPurposesMapping: Map<Product, Purpose>
        get() = Collections.unmodifiableMap(purposesByProduct)

    /**
     * Returns current consents by [Purpose]
     */
    @Suppress("unused") // Public API.
    val consents: Map<Purpose, Consent>
        get() {
            if (!consentConfiguration.requireConsent) {
                return emptyMap()
            }
            val noChanges = changedConsents.isEmpty()
            return productsByPurpose.mapValues {
                Consent(
                    if (noChanges) ConsentMode.NOT_ACQUIRED else changedConsents[it.key] ?: ConsentMode.OPT_IN,
                    it.value
                )
            }
        }

    /**
     * Resets consents to initial state
     */
    @Suppress("unused") // Public API.
    fun clear() {
        if (!consentConfiguration.requireConsent) {
            return
        }
        changedConsents.clear()
        purposesByProduct.clear()
        purposesByProduct.putAll(DEFAULT_PURPOSES_MAP)
        updateDefaultPurposes()
        save()
    }

    /**
     * Sets [mode] for [purpose] with updating purpose for [products], if supplied
     */
    @Suppress("unused") // Public API.
    fun set(purpose: Purpose, mode: ConsentMode, vararg products: Product) {
        check(consentConfiguration.requireConsent) {
            "You can't set consents because you've disabled its requirement"
        }
        require(mode != ConsentMode.NOT_ACQUIRED) {
            "You can't set NOT_ACQUIRED mode"
        }
        require(purpose in purposesByProduct.values || products.isNotEmpty()) {
            "You should provide at least one product for purpose" +
                " or define it via `ConsentConfiguration.defaultPurposes`"
        }
        changedConsents[purpose] = mode
        products.forEach {
            purposesByProduct[it] = purpose
        }
        save()
    }

    /**
     * Sets [mode] for all purposes
     */
    @Suppress("unused") // Public API.
    fun setAll(mode: ConsentMode) {
        check(consentConfiguration.requireConsent) {
            "You can't set consents because you've disabled its requirement"
        }
        require(mode != ConsentMode.NOT_ACQUIRED) {
            "You can't set NOT_ACQUIRED mode"
        }
        productsByPurpose.mapValuesTo(changedConsents) { mode }
        save()
    }

    companion object {
        @JvmStatic
        internal val DEFAULT_PURPOSES_MAP = mapOf(
            Product.PA to Purpose.AUDIENCE_MEASUREMENT,
            Product.DMP to Purpose.ADVERTISING,
            Product.COMPOSER to Purpose.CONTENT_PERSONALISATION,
            Product.ID to Purpose.PERSONAL_RELATIONSHIP,
            Product.VX to Purpose.PERSONAL_RELATIONSHIP,
            Product.ESP to Purpose.PERSONAL_RELATIONSHIP,
            Product.SOCIAL_FLOW to Purpose.ADVERTISING
        )

        @JvmStatic
        @Volatile
        private var instance: PianoConsents? = null

        /**
         * Initialize Consent Storage
         * @param context Activity or Application context.
         * @param consentConfiguration [ConsentConfiguration] instance with default settings
         */
        @JvmStatic
        @JvmOverloads
        @Suppress("unused") // Public API.
        fun init(
            context: Context,
            consentConfiguration: ConsentConfiguration = ConsentConfiguration(),
        ): PianoConsents {
            if (instance == null) {
                synchronized(this) {
                    if (instance == null) {
                        val moshi = Moshi.Builder()
                            .add(ConsentJsonAdapterFactory)
                            .build()
                        val purposesAdapter: JsonAdapter<Map<Product, Purpose>> =
                            moshi.adapter(
                                Types.newParameterizedType(
                                    Map::class.java,
                                    Product::class.java,
                                    Purpose::class.java
                                )
                            )
                        val consentModesAdapter: JsonAdapter<Map<Purpose, ConsentMode>> =
                            moshi.adapter(
                                Types.newParameterizedType(
                                    Map::class.java,
                                    Purpose::class.java,
                                    ConsentMode::class.java
                                )
                            )
                        instance = PianoConsents(
                            consentConfiguration,
                            PrefsStorage(context.applicationContext),
                            purposesAdapter,
                            consentModesAdapter
                        )
                    }
                }
            }
            return getInstance()
        }

        /**
         * Retrieves the singleton instance of the Consent Storage.
         * @return The singleton instance of [PianoConsents]
         */
        @JvmStatic
        @Suppress("unused") // Public API.
        fun getInstance(): PianoConsents {
            checkNotNull(instance) {
                "Piano Consent Storage is not initialized! Make sure that you initialize it"
            }
            return instance as PianoConsents
        }
    }
}
