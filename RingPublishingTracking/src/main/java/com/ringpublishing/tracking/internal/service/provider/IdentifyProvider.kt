package com.ringpublishing.tracking.internal.service.provider

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.TrackingIdentifierError
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.ringpublishing.tracking.internal.service.builder.IdentifyRequestBuilder
import com.ringpublishing.tracking.internal.service.logIdentifyResponse
import com.ringpublishing.tracking.internal.service.result.ReportEventResult
import com.ringpublishing.tracking.internal.service.result.ReportEventStatus
import com.ringpublishing.tracking.internal.service.result.ReportEventStatusMapper
import com.ringpublishing.tracking.internal.util.isIdentifyExpire
import retrofit2.Response

internal class IdentifyProvider(
    private val apiClient: ApiClient,
    private val reportEventStatusMapper: ReportEventStatusMapper,
    private val userRepository: UserRepository,
    private val apiRepository: ApiRepository,
) {

    var identifyResponse: IdentifyResponse? = null
        private set

    fun shouldRequestIdentify(): Boolean
    {
        if (identifyResponse == null)
        {
            identifyResponse = apiRepository.readIdentify() ?: return true
        }

        if (identifyResponse?.getValidDate(apiRepository.readIdentifyRequestDate()).isIdentifyExpire())
        {
            apiRepository.removeIdentify()
            identifyResponse = null
            return true
        }

        return false
    }

    suspend fun requestIdentify(): ReportEventResult
    {
        runCatching {
            val response = onIdentifyRequest()
            return identifyResponse?.let {
                return onNewIdentifySaved(it, response)
            } ?: kotlin.run {
                onIdentifyBodyFailed()
                return parseIdentifyResponse()
            }
        }.getOrElse {
            onIdentifyNetworkRequestError(it)
            return parseIdentifyResponse()
        }
    }

    private suspend fun onIdentifyRequest(): Response<IdentifyResponse>
    {
        "Start Request identity".toDebugLog()

        val requestBuilder = IdentifyRequestBuilder(userRepository.buildUser(), apiRepository.readIdentify())
        val response = apiClient.identify(requestBuilder.build())
        val success = response.isSuccessful

        Logger.logIdentifyResponse(success, response)
        handleRequestIdentify(success, response)
        return response
    }

    private fun handleRequestIdentify(success: Boolean, response: Response<IdentifyResponse>)
    {
        if (success)
        {
            identifyResponse = response.body()
            RingPublishingTracking.trackingIdentifierError = null
        } else {
            RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.REQUEST_ERROR
        }
    }

    private fun onNewIdentifySaved(identify: IdentifyResponse, response: Response<IdentifyResponse>): ReportEventResult
    {
        var eventResult = ReportEventResult(ReportEventStatus.ERROR_BAD_RESPONSE)
        if (identify.getIdentifier() != null && identify.getLifetime() > 0)
        {
            apiRepository.saveIdentify(identify)
            "New identify saved".toDebugLog()
            eventResult = ReportEventResult(reportEventStatusMapper.getStatus(response.code()), response.body()?.postInterval)
        } else {
            reportResponseError()
        }

        return eventResult
    }

    private fun onIdentifyBodyFailed()
    {
        identifyResponse = apiRepository.readIdentify()
        RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.RESPONSE_ERROR
        "Failed identify body. Try use saved identify: $identifyResponse".toDebugLog()
    }

    private fun parseIdentifyResponse() = identifyResponse?.let { identify ->
        ReportEventResult(ReportEventStatus.SUCCESS, identify.postInterval)
    } ?: reportConnectionError()

    private fun reportResponseError(): ReportEventResult
    {
        RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.RESPONSE_ERROR
        return ReportEventResult(ReportEventStatus.ERROR_BAD_RESPONSE)
    }

    private fun reportConnectionError(): ReportEventResult
    {
        RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.CONNECTION_ERROR
        return ReportEventResult(ReportEventStatus.ERROR_NETWORK)
    }

    private fun onIdentifyNetworkRequestError(it: Throwable)
    {
        "Identify request network error ${it.localizedMessage}".toWarnLog()
        identifyResponse = apiRepository.readIdentify()
        "Try read last saved identify after fail request. Saved identify: $identifyResponse".toDebugLog()
    }

    private fun String.toDebugLog() = Logger.debug("IdentifyProvider: $this")

    private fun String.toWarnLog() = Logger.warn("IdentifyProvider: $this")
}
