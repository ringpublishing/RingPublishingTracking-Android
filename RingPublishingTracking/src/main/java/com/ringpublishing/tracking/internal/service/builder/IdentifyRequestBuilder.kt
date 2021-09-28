package com.ringpublishing.tracking.internal.service.builder

import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.request.IdentifyRequest

internal class IdentifyRequestBuilder(
    private val user: User?
)
{

    fun build(): IdentifyRequest
    {
        val ids = mutableMapOf<String, String>()
        return IdentifyRequest(ids, user)
    }
}
