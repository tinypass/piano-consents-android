package io.piano.android.consents

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import io.piano.android.consents.models.ConsentMode
import io.piano.android.consents.models.Product
import io.piano.android.consents.models.Purpose
import java.lang.reflect.Type

internal object ConsentModeJsonAdapter : JsonAdapter<ConsentMode>() {
    override fun fromJson(reader: JsonReader): ConsentMode? {
        val alias = reader.nextString()
        return ConsentMode.values().first { it.alias == alias }
    }

    override fun toJson(writer: JsonWriter, value: ConsentMode?) {
        writer.value(value?.alias)
    }
}

internal object ProductJsonAdapter : JsonAdapter<Product>() {
    override fun fromJson(reader: JsonReader): Product? {
        val alias = reader.nextString()
        return Product.values().first { it.alias == alias }
    }

    override fun toJson(writer: JsonWriter, value: Product?) {
        writer.value(value?.alias)
    }
}

internal object PurposeJsonAdapter : JsonAdapter<Purpose>() {
    override fun fromJson(reader: JsonReader): Purpose? =
        when (val alias = reader.nextString()) {
            Purpose.ALIAS_AUDIENCE_MEASUREMENT -> Purpose.AUDIENCE_MEASUREMENT
            Purpose.ALIAS_CONTENT_PERSONALISATION -> Purpose.CONTENT_PERSONALISATION
            Purpose.ALIAS_ADVERTISING -> Purpose.ADVERTISING
            Purpose.ALIAS_PERSONAL_RELATIONSHIP -> Purpose.PERSONAL_RELATIONSHIP
            null -> null
            else -> Purpose(alias)
        }

    override fun toJson(writer: JsonWriter, value: Purpose?) {
        writer.value(value?.alias)
    }
}

object ConsentJsonAdapterFactory : JsonAdapter.Factory {
    override fun create(type: Type, annotations: MutableSet<out Annotation>, moshi: Moshi): JsonAdapter<*>? =
        when (type) {
            ConsentMode::class.java -> ConsentModeJsonAdapter
            Product::class.java -> ProductJsonAdapter
            Purpose::class.java -> PurposeJsonAdapter
            else -> null
        }
}
