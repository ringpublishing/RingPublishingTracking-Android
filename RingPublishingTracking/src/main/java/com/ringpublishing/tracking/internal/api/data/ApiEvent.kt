package com.ringpublishing.tracking.internal.api.data

import com.google.gson.annotations.SerializedName
import java.util.SortedMap

internal data class ApiEvent(
    @SerializedName("ac")
    val clientId: String,
    @SerializedName("et")
    val eventType: String,
    val data: SortedMap<String, Any>
)
