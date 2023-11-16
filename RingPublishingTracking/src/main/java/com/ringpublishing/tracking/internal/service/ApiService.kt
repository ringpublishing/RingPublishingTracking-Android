package com.ringpublishing.tracking.internal.service

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.ConfigurationManager
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.ringpublishing.tracking.internal.service.builder.EventRequestBuilder
import com.ringpublishing.tracking.internal.service.provider.ArtemisIdProvider
import com.ringpublishing.tracking.internal.service.provider.IdentifyProvider
import com.ringpublishing.tracking.internal.service.result.ReportEventResult
import com.ringpublishing.tracking.internal.service.result.ReportEventStatus
import com.ringpublishing.tracking.internal.service.result.ReportEventStatusMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

internal class ApiService(
    private val apiClient: ApiClient,
    private val reportEventStatusMapper: ReportEventStatusMapper,
    private val userRepository: UserRepository,
    configurationManager: ConfigurationManager,
    private val apiRepository: ApiRepository,
) {

    private val identifyProvider = IdentifyProvider(
        apiClient = apiClient,
        reportEventStatusMapper = reportEventStatusMapper,
        userRepository = userRepository,
        apiRepository = apiRepository,
    )

    private val artemisProvider = ArtemisIdProvider(
        apiClient = apiClient,
        reportEventStatusMapper = reportEventStatusMapper,
        apiRepository = apiRepository,
        configurationManager = configurationManager,
    )

    private val getIdentifiersOnAppStartRequest: Job = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch(Dispatchers.IO)
    {
        if (identifyProvider.requestIdentify().isSuccess() && artemisProvider.requestArtemisId().isSuccess())
        {
            updateTrackingIdentifier()
        }
    }

    suspend fun reportEvents(events: List<Event>): ReportEventResult
    {
        if (getIdentifiersOnAppStartRequest.isActive)
        {
            Logger.debug("ApiService: reportEvents() wait for first identify request")
            getIdentifiersOnAppStartRequest.join()
            Logger.debug("ApiService: reportEvents() continue work after first identify request")
        }
        if (identifyProvider.shouldRequestIdentify())
            {
                identifyProvider.requestIdentify().let {
                if (!it.isSuccess()) return it
            }
        }
        if (identifyProvider.shouldRequestIdentify() || artemisProvider.shouldRequestArtemisId())
        {
            artemisProvider.requestArtemisId().let {
                if (!it.isSuccess()) return it
                else updateTrackingIdentifier()
            }
        }
        return requestEvents(events)
    }

    private suspend fun requestEvents(events: List<Event>): ReportEventResult
    {
        runCatching {
            Logger.debug("ApiService: Start Request events")
            val requestBuilder = EventRequestBuilder(events, identifyProvider.identifyResponse!!, userRepository.buildUser())

            val response = apiClient.send(requestBuilder.build())
            val success = response.isSuccessful
            Logger.logEventResponse(success, response)

            return ReportEventResult(reportEventStatusMapper.getStatus(response.code()), response.body()?.postInterval)
        }.getOrElse {
            Logger.warn("ApiService: Send request Network error ${it.localizedMessage}")
            return ReportEventResult(ReportEventStatus.ERROR_NETWORK)
        }
    }

    private fun updateTrackingIdentifier()
    {
        RingPublishingTracking.trackingIdentifier = apiRepository.readTrackingIdentifier()
        RingPublishingTracking.trackingIdentifierError = null
    }

    fun hasIdentify() = identifyProvider.identifyResponse != null && artemisProvider.artemisIdResponse != null
}
