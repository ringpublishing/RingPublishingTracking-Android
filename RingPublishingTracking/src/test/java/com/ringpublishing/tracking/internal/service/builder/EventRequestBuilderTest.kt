/*
 *  Created by Grzegorz Małopolski on 10/26/21, 5:26 PM
 * Copyright © 2021 Ringier Axel Springer Tech. All rights reserved.
 *
 */

package com.ringpublishing.tracking.internal.service.builder

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.ringpublishing.tracking.data.Event
import com.ringpublishing.tracking.internal.api.data.IdsMap
import com.ringpublishing.tracking.internal.api.data.User
import com.ringpublishing.tracking.internal.api.response.IdentifyResponse
import com.ringpublishing.tracking.internal.log.Logger
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Assert
import org.junit.Before
import org.junit.Test

internal class EventRequestBuilderTest
{

	@MockK
	lateinit var event1: Event

	@MockK
	lateinit var identifyResponse: IdentifyResponse

	@MockK
	lateinit var user: User

	@MockK
	lateinit var idsMap: IdsMap

	@Before
	fun before()
	{
		MockKAnnotations.init(this, relaxUnitFun = true)
		Logger.debugLogEnabled(false)
	}

	@Test
	fun build_WhenAllParameters_ThenAllInCreatedEventRequest()
	{
		every { event1.analyticsSystemName } returns "analyticsSystemName"
		every { event1.name } returns "name"

		val parameters = mutableMapOf<String, Any>()
		parameters["first"] = "first_value"

		every { event1.parameters } returns parameters

		val events = mutableListOf<Event>()
		events.add(event1)

		every { identifyResponse.ids } returns idsMap

		val parametersJson = mutableMapOf<String, JsonElement>()
		parametersJson["first"] = JsonObject()

		every { idsMap.parameters } returns parametersJson

		val builder = EventRequestBuilder(events, identifyResponse, user)

		val event = builder.build()

		Assert.assertTrue(event.events.isNotEmpty())
		Assert.assertNotNull(event.ids)
		Assert.assertNotNull(event.user)
	}
}
