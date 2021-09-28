package com.ringpublishing.tracking.internal.api.request

import com.ringpublishing.tracking.internal.api.data.ApiEvent
import com.ringpublishing.tracking.internal.api.data.Ids
import com.ringpublishing.tracking.internal.api.data.User

internal data class EventRequest(
    val events: Array<ApiEvent>,
    val ids: Ids,
    val user: User
)
{

    override fun equals(other: Any?): Boolean
    {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EventRequest

        if (!events.contentEquals(other.events)) return false
        if (ids != other.ids) return false
        if (user != other.user) return false

        return true
    }

    override fun hashCode(): Int
    {
        var result = events.contentHashCode()
        result = 31 * result + ids.hashCode()
        result = 31 * result + user.hashCode()
        return result
    }
}
