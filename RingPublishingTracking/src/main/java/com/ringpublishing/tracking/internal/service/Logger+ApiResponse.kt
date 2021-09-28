package com.ringpublishing.tracking.internal.service

import com.ringpublishing.tracking.internal.api.response.EventResponse
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.log.Logger
import retrofit2.Response

internal fun Logger.logEventResponse(success: Boolean, response: Response<EventResponse>)
{
    if (success)
    {
        debug("EventApi: Send Event response result success=$response.body()")
    } else {
        warn("EventApi: Send Event response result ${response.errorBody()} Error code=${response.code()}")
    }
}

internal fun Logger.logIdentifyResponse(success: Boolean, response: Response<IdentifyResponse>)
{
    if (success)
    {
        debug("EventApi: Identify response result success=$response.body()")
    } else {
        warn("EventApi: Identify response result ${response.errorBody()} Error code=${response.code()}")
    }
}
