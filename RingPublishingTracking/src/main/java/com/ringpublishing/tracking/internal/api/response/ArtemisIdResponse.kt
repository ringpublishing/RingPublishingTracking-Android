package com.ringpublishing.tracking.internal.api.response

import com.google.gson.JsonElement
import com.ringpublishing.tracking.data.ArtemisId
import com.ringpublishing.tracking.data.External
import java.util.Calendar
import java.util.Date

internal data class ArtemisIdResponse(
    val cfg: Cfg?,
    val user: User?
) {
    fun toArtemisId() = ArtemisId(
        artemis = user?.id?.real,
        external = External(
            model = user?.id?.model,
            models = user?.id?.models
        )
    )

    fun getLifetime() = cfg?.ttl ?: 0

    fun getValidDate(savedIdentifyDate: Date?): Date? {
        if (savedIdentifyDate == null) return null

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = savedIdentifyDate.time + ((cfg?.ttl ?: 0) * 1000)
        return calendar.time
    }
}

internal data class Cfg(
    val version: Double?,
    val ttl: Long?,
    val cmds: List<Any?>
)

internal data class User(
    val id: Id?
)

internal data class Id(
    val real: String?,
    val model: String?,
    val models: JsonElement?
)
