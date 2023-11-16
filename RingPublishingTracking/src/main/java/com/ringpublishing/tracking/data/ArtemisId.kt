package com.ringpublishing.tracking.data

import com.google.gson.JsonElement

data class ArtemisId(
    val artemis: String?,
    val external: External?
)

data class External(
    val model: String?,
    val models: JsonElement?
)
