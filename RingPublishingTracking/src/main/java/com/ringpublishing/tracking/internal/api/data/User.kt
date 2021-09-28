package com.ringpublishing.tracking.internal.api.data

import com.google.gson.annotations.SerializedName

internal data class User(
    @SerializedName("advId")
    val advertisementId: String? = null,
    val deviceId: String? = null
)
