/*
 *  Created by Grzegorz Małopolski on 10/28/21, 3:46 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.com.ringpublishing.tracking.internal.service.queue

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.constants.Constants
import com.ringpublishing.tracking.internal.log.Logger
import com.ringpublishing.tracking.internal.repository.ApiRepository
import com.ringpublishing.tracking.internal.repository.UserRepository
import com.ringpublishing.tracking.internal.service.queue.EventSizeCalculator
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class EventSizeCalculatorTest
{

	@MockK
	internal lateinit var apiRepository: ApiRepository

	@MockK
	internal lateinit var userRepository: UserRepository

	@MockK
	internal lateinit var gson: Gson

	@MockK
	internal lateinit var identifyResponse: IdentifyResponse

	@MockK
	internal lateinit var user: User

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(true)
	}

	@Test
	fun calculateBodyElementsSize_WhenCalculateObject_ThenSizeIsChange()
	{
		every { apiRepository.readIdentify() } returns identifyResponse
		every { gson.toJson(identifyResponse) } returns "{1234}"
		every { userRepository.buildUser() } returns user
		every { gson.toJson(user) } returns "{user}"

		val eventSizeCalculator = EventSizeCalculator(gson, apiRepository, userRepository)

		val availableBefore = eventSizeCalculator.available(0)

		Assert.assertEquals(Constants.maxRequestBodySize, availableBefore)

		eventSizeCalculator.calculateBodyElementsSize()

		val available = eventSizeCalculator.available(0)

		Assert.assertNotEquals(Constants.maxRequestBodySize, available)
	}

	@Test
	fun available_WhenAddEvent_ThenSizeChange()
	{
		val eventSizeCalculator = EventSizeCalculator(gson, apiRepository, userRepository)

		val available = eventSizeCalculator.available(100)

		Assert.assertEquals(Constants.maxRequestBodySize - 100, available)
	}

	@Test
	fun isBiggerThanMaxRequestSize()
	{
		val eventSizeCalculator = EventSizeCalculator(gson, apiRepository, userRepository)

		val isLower = eventSizeCalculator.isBiggerThanMaxRequestSize(200, 100)

		Assert.assertFalse(isLower)
	}

	@Test
	fun getSizeInBytes_WhenHaveEvent_ThenSizeNoBiggerThanExpected()
	{
		val gson = GsonBuilder().create()

		val map = mutableMapOf<String, Any>()
		map["k"] = "ć"
		val event = Event("a", "ż", map)
		val eventSizeCalculator = EventSizeCalculator(gson, apiRepository, userRepository)

		val size = eventSizeCalculator.getSizeInBytes(event)

		Assert.assertTrue(size <= 122)
	}
}
