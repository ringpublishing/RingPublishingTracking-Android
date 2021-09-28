package com.ringpublishing.tracking.internal.api

import com.ringpublishing.tracking.internal.api.request.EventRequest
import com.ringpublishing.tracking.internal.api.request.IdentifyRequest
import com.ringpublishing.tracking.internal.api.response.EventResponse
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.constants.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface ApiDefinition
{

    @POST("${Constants.apiVersion}/me")
    suspend fun identify(@Query("_key") apiKey: String?, @Body body: IdentifyRequest?): Response<IdentifyResponse>

    @POST(Constants.apiVersion)
    suspend fun send(@Query("_key") apiKey: String?, @Body body: EventRequest?): Response<EventResponse>
}
