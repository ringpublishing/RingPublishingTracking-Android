package com.ringpublishing.tracking.internal.service

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.TrackingIdentifierError
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.ringpublishing.tracking.internal.service.builder.EventRequestBuilder
import com.ringpublishing.tracking.internal.service.builder.IdentifyRequestBuilder
import com.ringpublishing.tracking.internal.service.result.ReportEventResult
import com.ringpublishing.tracking.internal.service.result.ReportEventStatus
import com.ringpublishing.tracking.internal.service.result.ReportEventStatusMapper
import com.ringpublishing.tracking.internal.util.isIdentifyExpire
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import retrofit2.Response

internal class ApiService(
	private val apiClient: ApiClient,
	private val reportEventStatusMapper: ReportEventStatusMapper,
	private val apiRepository: ApiRepository,
	private val userRepository: UserRepository,
)
{

	private var identifyResponse: IdentifyResponse? = null
	private var firstRequestIdentifyResult: ReportEventResult? = null

	private val appStartIdentifyRequest: Job = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(Dispatchers.IO) {
		Logger.debug("ApiService: First identify start request")
		firstRequestIdentifyResult = requestIdentify()
		Logger.debug("ApiService: First identify end request")

		firstRequestIdentifyResult?.let {
			if (it.isSuccess())
			{
				RingPublishingTracking.trackingIdentifier = apiRepository.readTrackingIdentifier()
				RingPublishingTracking.trackingIdentifierError = null
			}
		}
	}

	suspend fun reportEvents(events: List<Event>): ReportEventResult
	{
		if (firstRequestIdentifyResult == null)
		{
			"reportEvents() wait for first identify request".toDebugLog()
			appStartIdentifyRequest.join()
			"ApiService: reportEvents() continue work after first identify request".toDebugLog()
		}

		if (!shouldRequestIdentify())
		{
			return requestEvents(events)
		}

		val identifyStatus = requestIdentify()

		if (!identifyStatus.isSuccess())
		{
			return identifyStatus
		}

		return requestEvents(events)
	}

	private fun shouldRequestIdentify(): Boolean
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

	private suspend fun requestEvents(events: List<Event>): ReportEventResult
	{
		runCatching {
			"Start Request events".toDebugLog()
			val requestBuilder = EventRequestBuilder(events, identifyResponse!!, userRepository.buildUser())

			val response = apiClient.send(requestBuilder.build())
			val success = response.isSuccessful
			Logger.logEventResponse(success, response)

			return ReportEventResult(reportEventStatusMapper.getStatus(response.code()), response.body()?.postInterval)
		}.getOrElse {
			"Send request Network error ${it.localizedMessage}".toWarnLog()
			return ReportEventResult(ReportEventStatus.ERROR_NETWORK)
		}
	}

	private suspend fun requestIdentify(): ReportEventResult
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
		}
		else
		{
			RingPublishingTracking.trackingIdentifierError = TrackingIdentifierError.REQUEST_ERROR
		}
	}

	private fun onNewIdentifySaved(identify: IdentifyResponse, response: Response<IdentifyResponse>): ReportEventResult
	{
		var eventResult = ReportEventResult(ReportEventStatus.ERROR_BAD_RESPONSE)
		identify.ids?.parameters?.let {
			if(it.containsKey("value") && it.containsKey("lifetime")) {
				apiRepository.saveIdentify(identify)
				"New identify saved".toDebugLog()
				 eventResult = ReportEventResult(reportEventStatusMapper.getStatus(response.code()), response.body()?.postInterval)
			} else {
				reportResponseError()
			}
		} ?: run {
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

	private fun String.toDebugLog()
	{
		Logger.debug("ApiService: $this")
	}

	private fun String.toWarnLog()
	{
		Logger.warn("ApiService: $this")
	}

	fun hasIdentify() = identifyResponse != null
}
