package com.ringpublishing.tracking.internal.api.serialization

import com.ringpublishing.tracking.internal.api.data.IdsMap
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

internal class IdsMapDeserializer : JsonDeserializer<IdsMap>
{
    @Throws(JsonParseException::class)
    override fun deserialize(element: JsonElement, arg1: Type?, arg2: JsonDeserializationContext?): IdsMap
    {
        val parameters = mutableMapOf<String, JsonElement>()

        element.asJsonObject.entrySet().forEach {
            parameters[it.key] = it.value
        }

        return IdsMap(parameters)
    }
}
