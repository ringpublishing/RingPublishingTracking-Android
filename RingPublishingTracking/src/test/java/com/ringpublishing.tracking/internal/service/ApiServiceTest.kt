package com.ringpublishing.tracking.internal.service

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.ringpublishing.tracking.internal.service.result.ReportEventStatusMapper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ApiServiceTest
{

	@MockK
	lateinit var event: Event

	@MockK
	internal lateinit var apiClient: ApiClient

	@MockK
	internal lateinit var reportEventStatusMapper: ReportEventStatusMapper

	@MockK
	internal lateinit var apiRepository: ApiRepository

	@MockK
	internal lateinit var userRepository: UserRepository

	@MockK
	internal lateinit var identifyResponse: IdentifyResponse

	@MockK
	internal lateinit var user: User

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		RingPublishingTracking.setDebugMode(true)
	}

	@Test
	fun reportEvents_NoSavedIdentifyDate_IdentifyApiWasCalled()
	{
		coEvery { apiRepository.readIdentify() } returns identifyResponse
		coEvery { apiRepository.readIdentifyRequestDate() } returns null

		coEvery { identifyResponse.getValidDate(any()) } returns null

		coEvery { identifyResponse.postInterval } returns 500
		coEvery { userRepository.buildUser() } returns user

		val apiService = ApiService(apiClient, reportEventStatusMapper, apiRepository, userRepository)

		val events = mutableListOf(event)

		runBlocking {
			apiService.reportEvents(events)
		}
		coVerify(exactly = 2) { apiClient.identify(any()) }
	}

	@Test
	fun reportEvents_NoSavedIdentify_IdentifyApiWasCalled()
	{
		coEvery { apiRepository.readIdentify() } returns null
		coEvery { userRepository.buildUser() } returns user

		val apiService = ApiService(apiClient, reportEventStatusMapper, apiRepository, userRepository)

		val events = mutableListOf(event)

		runBlocking {
			apiService.reportEvents(events)
		}
		coVerify(exactly = 2) { apiClient.identify(any()) }
	}
}
