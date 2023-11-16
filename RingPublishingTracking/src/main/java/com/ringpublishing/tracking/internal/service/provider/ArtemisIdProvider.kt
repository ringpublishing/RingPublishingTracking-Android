package com.ringpublishing.tracking.internal.service.provider

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.TrackingIdentifierError
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.api.request.ArtemisIdRequest
import com.ringpublishing.tracking.internal.api.request.User
import com.ringpublishing.tracking.internal.api.request.UserId
import com.ringpublishing.tracking.internal.api.request.UserSso
import com.ringpublishing.tracking.internal.api.response.ArtemisIdResponse
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.service.logArtemisIdResponse
import com.ringpublishing.tracking.internal.service.result.ReportEventResult
import com.ringpublishing.tracking.internal.service.result.ReportEventStatus
import com.ringpublishing.tracking.internal.service.result.ReportEventStatusMapper
import com.ringpublishing.tracking.internal.util.isIdentifyExpire
import retrofit2.Response

internal class ArtemisIdProvider(
    private val apiClient: ApiClient,
    private val reportEventStatusMapper: ReportEventStatusMapper,
    private val apiRepository: ApiRepository,
    private val configurationManager: ConfigurationManager
) {

    var artemisIdResponse: ArtemisIdResponse? = null
        private set

    fun shouldRequestArtemisId(): Boolean
    {
        if (artemisIdResponse == null)
        {
            artemisIdResponse = apiRepository.readArtemisId() ?: return true
        }

        if (artemisIdResponse?.getValidDate(apiRepository.readArtemisIdDate()).isIdentifyExpire())
        {
            apiRepository.removeArtemisId()
            artemisIdResponse = null
            return true
        }

        return false
    }

    suspend fun requestArtemisId(): ReportEventResult
    {
        runCatching{
            val response = onArtemisIdRequest()
            return artemisIdResponse?.let {
                return onNewArtemisIdSaved(it, response)
            } ?: kotlin.run {
                onArtemisIdBodyFailed()
                return parseArtemisIdResponse()
            }
        }.getOrElse {
            onArtemisIdNetworkRequestError(it)
            return parseArtemisIdResponse()
        }
    }

    private suspend fun onArtemisIdRequest(): Response<ArtemisIdResponse>?
    {
        "Start Request getArtemisId".toDebugLog()

        val identifier = apiRepository.readIdentify()?.getIdentifier() ?: let {
             return null
        }
        val userId = UserId(
            local = identifier,
            global = identifier
        )
        val userSso = UserSso(
            name = configurationManager.getUserData().ssoName,
            id = configurationManager.getUserData().userId
        )
        val request = ArtemisIdRequest(
            user = User(userId, userSso),
            tid = configurationManager.getTenantId()
        )
        val response = apiClient.getArtemisId(request)
        val success = response.isSuccessful

        Logger.logArtemisIdResponse(success, response)

        handleArtemisIdResponse(success, response)
        return response
    }

    private fun handleArtemisIdResponse(success: Boolean, response: Response<ArtemisIdResponse>)
    {
        if (success)
        {
            artemisIdResponse = response.body()
            RingPublishingTracking.trackingIdentifierError = null
        } else {
            RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.REQUEST_ERROR
        }
    }

    private fun onNewArtemisIdSaved(artemisId: ArtemisIdResponse, response: Response<ArtemisIdResponse>?): ReportEventResult
    {
        var eventResult = ReportEventResult(ReportEventStatus.ERROR_BAD_RESPONSE)
        if (response != null && artemisId.getLifetime() > 0)
        {
            apiRepository.saveArtemisId(artemisId)
            "New ArtemisId saved".toDebugLog()
            eventResult = ReportEventResult(reportEventStatusMapper.getStatus(response.code()))
        } else {
            reportResponseError()
        }

        return eventResult
    }

    private fun reportResponseError(): ReportEventResult
    {
        RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.RESPONSE_ERROR
        return ReportEventResult(ReportEventStatus.ERROR_BAD_RESPONSE)
    }

    private fun onArtemisIdBodyFailed()
    {
        artemisIdResponse = apiRepository.readArtemisId()
        RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.RESPONSE_ERROR
        "Failed artemisId body. Try use saved identify: $artemisIdResponse".toDebugLog()
    }

    private fun parseArtemisIdResponse() = artemisIdResponse?.let{
        ReportEventResult(ReportEventStatus.SUCCESS)
    } ?: reportConnectionError()

    private fun reportConnectionError(): ReportEventResult
    {
        RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.CONNECTION_ERROR
        return ReportEventResult(ReportEventStatus.ERROR_NETWORK)
    }

    private fun onArtemisIdNetworkRequestError(it: Throwable)
    {
        "ArtemisId request request network error ${it.localizedMessage}".toWarnLog()
        artemisIdResponse = apiRepository.readArtemisId()
        "Try read last saved artemisId after fail request. Saved identify: $artemisIdResponse".toDebugLog()
    }

    private fun String.toDebugLog() = Logger.debug("ArtemisIdProvider: $this")

    private fun String.toWarnLog() = Logger.warn("ArtemisIdProvider: $this")
}
