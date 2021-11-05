package com.ringpublishing.tracking.internal.service.builder

import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.request.IdentifyRequest
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse

internal class IdentifyRequestBuilder(
    private val user: User? = null,
    private val oldRequest: IdentifyResponse? = null
)
{

    fun build(): IdentifyRequest
    {
        val ids = mutableMapOf<String, String>()

	    oldRequest?.let { identifyResponse ->
		    val identifier = identifyResponse.getIdentifier()
		    identifier?.let {
			    ids["eaUUID"] = it
		    }
	    }
        return IdentifyRequest(ids, user)
    }
}
