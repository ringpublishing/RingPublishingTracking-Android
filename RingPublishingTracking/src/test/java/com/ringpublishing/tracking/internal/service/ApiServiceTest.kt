/*
 *  Created by Grzegorz Małopolski on 10/28/21, 3:46 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.service

import com.ringpublishing.tracking.RingPublishingTracking
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.api.ApiClient
import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.ringpublishing.tracking.internal.service.ApiService
import com.ringpublishing.tracking.internal.service.result.ReportEventStatusMapper
import com.ringpublishing.tracking.internal.util.isIdentifyExpire
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockkStatic
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.Date

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

	@MockK
	internal var expiryDate: Date = Date(Date().time + 10000)

	@Before
	fun before()
	{
		mockkStatic("com.ringpublishing.tracking.internal.util.Date_IndetifierExpireKt")
		MockKAnnotations.init(this, relaxUnitFun = true)
		RingPublishingTracking.setDebugMode(true)
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

	@Test
	fun reportEvents_SavedIdentify_IdentifyApiNotCalled()
	{
		coEvery { identifyResponse.getValidDate(any()) } returns expiryDate
		coEvery { expiryDate.isIdentifyExpire() } returns false
		coEvery { identifyResponse.postInterval } returns 500
		coEvery { apiRepository.readIdentify() } returns identifyResponse
		coEvery { apiRepository.readIdentifyRequestDate()} returns Date()
		coEvery { userRepository.buildUser() } returns user

		val apiService = ApiService(apiClient, reportEventStatusMapper, apiRepository, userRepository)

		val events = mutableListOf(event)

		runBlocking {
			apiService.reportEvents(events)
		}
		coVerify(exactly = 0) { apiClient.identify(any()) }
	}

	@Test
	fun reportEvents_SavedIdentifyExist_ThenNoSaveNewIdentify()
	{
		coEvery { identifyResponse.getValidDate(any()) } returns expiryDate
		coEvery { expiryDate.isIdentifyExpire() } returns false
		coEvery { identifyResponse.postInterval } returns 500
		coEvery { apiRepository.readIdentify() } returns identifyResponse
		coEvery { apiRepository.readIdentifyRequestDate() } returns Date()

		val apiService = ApiService(apiClient, reportEventStatusMapper, apiRepository, userRepository)

		val events = mutableListOf(event)

		runBlocking {
			apiService.reportEvents(events)
		}
		coVerify(exactly = 2) { userRepository.buildUser() }
		coVerify(exactly = 0) { apiRepository.saveIdentify(any()) }
	}
}
