package com.ringpublishing.tracking.internal.di

import com.ringpublishing.tracking.data.RingPublishingTrackingConfiguration
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.service.ApiService
import com.ringpublishing.tracking.internal.service.result.ReportEventStatusMapper
import java.net.URL

private var apiClient: ApiClient? = null

internal fun Component.provideApiClient(apiKey: String, apiUrl: URL): ApiClient
{
	if (apiClient == null)
	{
		apiClient = ApiClient(apiKey, apiUrl, Component.provideNetworkClient())
	}

	return apiClient!!
}

private var apiService: ApiService? = null

internal fun Component.provideApiService(ringPublishingTrackingConfiguration: RingPublishingTrackingConfiguration): ApiService
{
	if (apiService == null)
	{
		apiService = ApiService(
			Component.provideApiClient(ringPublishingTrackingConfiguration.apiKey, ringPublishingTrackingConfiguration.apiUrl ?: Constants.apiUrl),
			Component.provideReportEventStatusMapper(),
            Component.provideUserRepository(),
            Component.provideConfigurationManager(),
            Component.provideApiRepository(),
        )
	}

	return apiService!!
}

private val reportEventStatusMapper by lazy { ReportEventStatusMapper() }

internal fun Component.provideReportEventStatusMapper() = reportEventStatusMapper
