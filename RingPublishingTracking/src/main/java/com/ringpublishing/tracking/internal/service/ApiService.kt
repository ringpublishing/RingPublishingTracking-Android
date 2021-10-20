package com.ringpublishing.tracking.internal.service

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Date

internal class ApiService(
    private val apiClient: ApiClient,
    private val reportEventStatusMapper: ReportEventStatusMapper,
    private val apiRepository: ApiRepository,
    private val userRepository: UserRepository
)
{
    private var identifyResponse: IdentifyResponse? = null

	init
	{
		CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(Dispatchers.IO) { requestIdentify() }
	}

    suspend fun reportEvents(events: List<Event>): ReportEventResult
    {
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

        val identifyValidToDate = identifyResponse?.getValidDate(apiRepository.readIdentifyRequestDate())

        if (identifyValidToDate == null || identifyValidToDate.before(Date()))
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
            Logger.debug("ApiService: Start Request events")
            val requestBuilder = EventRequestBuilder(events, identifyResponse!!, userRepository.buildUser())

            val response = apiClient.send(requestBuilder.build())
            val success = response.isSuccessful
            Logger.logEventResponse(success, response)

            return ReportEventResult(reportEventStatusMapper.getStatus(response.code()), response.body()?.postInterval)
        }.getOrElse {
            Logger.warn("ApiService: Send request Network error ${it.localizedMessage}")
            return ReportEventResult(ReportEventStatus.ERROR_NETWORK)
        }
    }

    private suspend fun requestIdentify(): ReportEventResult
    {
        runCatching {
            Logger.debug("ApiService: Start Request identity")
            val requestBuilder = IdentifyRequestBuilder(userRepository.buildUser())
            val response = apiClient.identify(requestBuilder.build())
            val success = response.isSuccessful
            Logger.logIdentifyResponse(success, response)

            identifyResponse = if (success) response.body() else null

            apiRepository.saveIdentify(identifyResponse)

		        return ReportEventResult(reportEventStatusMapper.getStatus(response.code()), response.body()?.postInterval)
        }.getOrElse {
            Logger.warn("ApiService: Identify request network error ${it.localizedMessage}")
            return ReportEventResult(ReportEventStatus.ERROR_NETWORK)
        }
    }
}
