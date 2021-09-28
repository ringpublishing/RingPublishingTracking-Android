package com.ringpublishing.tracking.internal.api.request

import com.ringpublishing.tracking.internal.api.data.User

internal data class IdentifyRequest(
    val ids: Map<String, String>,
    val user: User?,
)
