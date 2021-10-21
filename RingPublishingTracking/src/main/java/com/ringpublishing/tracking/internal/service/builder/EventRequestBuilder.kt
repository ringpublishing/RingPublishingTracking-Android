package com.ringpublishing.tracking.internal.service.builder

import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.api.data.ApiEvent
import com.ringpublishing.tracking.internal.api.data.Ids
import com.ringpublishing.tracking.internal.api.data.IdsMap
import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.request.EventRequest
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import java.util.TreeMap

internal class EventRequestBuilder(
    private val events: List<Event>,
    private val identifyResponse: IdentifyResponse,
    private val user: User
)
{

    fun build(): EventRequest
    {
        val apiEvents = mutableListOf<ApiEvent>()

        events.forEach { apiEvents.add(ApiEvent(it.analyticsSystemName, it.name, TreeMap(it.parameters))) }

        val ids = Ids(identifyResponse.ids ?: IdsMap())
        return EventRequest(apiEvents.toTypedArray(), ids, user)
    }
}
